PROCS = -T 8
PACKAGE = clean package
#FLAGS = -DskipTests=true -ntp -Dorg.slf4j.simpleLogger.defaultLogLevel=WARN
FLAGS = -Dquarkus.package.type=uber-jar -ntp
RELEASE = main
REG = 100225593120.dkr.ecr.us-east-1.amazonaws.com
AWS_DEFAULT_REGION := us-east-1

ENV_NAME=curation-alpha

OPTS = $(PROCS) $(PACKAGE) $(FLAGS)

GIT_VERSION = $(shell git describe)

.PHONY: docker all

registry-docker-login:
ifneq ($(shell echo ${REG} | egrep "ecr\..+\.amazonaws\.com"),)
	@$(eval DOCKER_LOGIN_CMD=docker run --rm -it -v ~/.aws:/root/.aws amazon/aws-cli)
ifneq (${AWS_PROFILE},)
	@$(eval DOCKER_LOGIN_CMD=${DOCKER_LOGIN_CMD} --profile ${AWS_PROFILE})
endif
	@$(eval DOCKER_LOGIN_CMD=${DOCKER_LOGIN_CMD} ecr get-login-password --region=${AWS_DEFAULT_REGION} | docker login -u AWS --password-stdin https://${REG})
	${DOCKER_LOGIN_CMD}
endif

all: ui api

api:
	mvn ${OPTS}

ui:
	make -B -C src/main/cliapp
	make -B -C src/main/cliapp build

uirun:
	make -B -C src/main/cliapp run

uirunalpha:
	export API_URL=https://alpha-curation.alliancegenome.org; make -B -C src/main/cliapp run; unset API_URL

uirunbeta:
	export API_URL=https://beta-curation.alliancegenome.org; make -B -C src/main/cliapp run; unset API_URL

run:
	java -jar target/agr_curation_api-runner.jar

apirun:
	mvn compile quarkus:dev

docker:
	docker build --build-arg OVERWRITE_VERSION=${GIT_VERSION} -t ${REG}/agr_curation:${RELEASE} .
docker-push:
	docker push ${REG}/agr_curation:${RELEASE}
docker-run:
	docker run --rm -it -p 8080:8080 --network=curation ${REG}/agr_curation:${RELEASE}

debug:
	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5045 -jar target/agr_curation_api-bootable.jar

release:
	mvn release:prepare; mvn release:clean; git pull

test:
	mvn test

integration-test:
	mvn failsafe:integration-test

verify:
	mvn verify

set-app-version-as-git:
	mvn versions:set -DnewVersion=${GIT_VERSION}

reset-app-version:
	mvn versions:revert

#EB commands
.PHONY: eb-init eb-create eb-deploy eb-terminate

eb-init:
	eb init --region us-east-1 -p Docker curation-app

eb-create:
	@eb create ${ENV_NAME} \
	        --region=us-east-1 \
	        --cname="${ENV_NAME}" \
			--envvars QUARKUS_DATASOURCE_USERNAME=${QUARKUS_DATASOURCE_USERNAME},QUARKUS_DATASOURCE_PASSWORD=${QUARKUS_DATASOURCE_PASSWORD} \
			--elb-type application

eb-deploy:
	@eb deploy ${ENV_NAME}

eb-terminate:
	@eb terminate ${ENV_NAME}
