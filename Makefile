RELEASE = alpha

AWS_DEFAULT_REGION := us-east-1
AWS_ACCT_NR=100225593120
REG = ${AWS_ACCT_NR}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com
EB_S3_BUCKET=elasticbeanstalk-${AWS_DEFAULT_REGION}-${AWS_ACCT_NR}

NET?=dev
ENV_NAME?=curation-${NET}

GIT_VERSION ?= $(shell git describe --tags)

.PHONY: docker all

all: docker

clean:
	mvn clean

registry-docker-login:
ifneq ($(shell echo ${REG} | egrep "ecr\..+\.amazonaws\.com"),)
	@$(eval DOCKER_LOGIN_CMD=docker run --rm -it -v ~/.aws:/root/.aws amazon/aws-cli)
ifneq (${AWS_PROFILE},)
	@$(eval DOCKER_LOGIN_CMD=${DOCKER_LOGIN_CMD} --profile ${AWS_PROFILE})
endif
	@$(eval DOCKER_LOGIN_CMD=${DOCKER_LOGIN_CMD} ecr get-login-password --region=${AWS_DEFAULT_REGION} | docker login -u AWS --password-stdin https://${REG})
	${DOCKER_LOGIN_CMD}
endif

uirun:
	make -B -C src/main/cliapp
	make -B -C src/main/cliapp run

pretty:
	make -B -C src/main/cliapp pretty

uirunalpha:
	export API_URL=https://alpha-curation.alliancegenome.org; make -B -C src/main/cliapp run; unset API_URL

uirunbeta:
	export API_URL=https://beta-curation.alliancegenome.org; make -B -C src/main/cliapp run; unset API_URL

uiruneales:
	export API_URL=http://eales.rgd.mcw.edu:8080; make -B -C src/main/cliapp run; unset API_URL

uirunlomu:
	export API_URL=http://lomu.rgd.mcw.edu:8080; make -B -C src/main/cliapp run; unset API_URL

run: docker-run

apirun:
	mvn compile quarkus:dev -Djvm.args="-XX:ReservedCodeCacheSize=1g"

docker:
	docker build --build-arg OVERWRITE_VERSION=${GIT_VERSION} -t ${REG}/agr_curation:${RELEASE} .
docker-push: registry-docker-login
	docker push ${REG}/agr_curation:${RELEASE}
docker-run:
	docker run --rm -it -p 8080:8080 --network=curation ${REG}/agr_curation:${RELEASE}

debug:
	java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5045 -jar target/agr_curation_api-bootable.jar

checkstyle:
	mvn checkstyle:check

prettier-check:
	npx prettier 'src/main/cliapp/**/*.js' --check

prettier-write:
	npx prettier 'src/main/cliapp/**/*.js' --write

test:
	mvn test

integration-test:
	mvn -ntp clean package
	mvn -ntp failsafe:integration-test -Dokta.authentication=false
	mvn -ntp failsafe:verify

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
	@eb create ${ENV_NAME} --region=us-east-1 --cname="${ENV_NAME}" --elb-type application --envvars \
		BULK_DATA_LOADS_S3SECRETKEY=${BULK_DATA_LOADS_S3SECRETKEY},BULK_DATA_LOADS_S3PATHPREFIX=${BULK_DATA_LOADS_S3PATHPREFIX},BULK_DATA_LOADS_S3ACCESSKEY=${BULK_DATA_LOADS_S3ACCESSKEY},NET=${NET},OKTA_API_TOKEN=${OKTA_API_TOKEN},OKTA_CLIENT_ID=${OKTA_CLIENT_ID},OKTA_CLIENT_SECRET=${OKTA_CLIENT_SECRET},OKTA_URL=${OKTA_URL},QUARKUS_DATASOURCE_JDBC_URL=${QUARKUS_DATASOURCE_JDBC_URL},QUARKUS_DATASOURCE_PASSWORD=${QUARKUS_DATASOURCE_PASSWORD},QUARKUS_DATASOURCE_USERNAME=${QUARKUS_DATASOURCE_USERNAME},QUARKUS_ELASTICSEARCH_HOSTS=${QUARKUS_ELASTICSEARCH_HOSTS},QUARKUS_ELASTICSEARCH_PROTOCOL=${QUARKUS_ELASTICSEARCH_PROTOCOL},QUARKUS_HIBERNATE_SEARCH_ORM_ELASTICSEARCH_HOSTS=${QUARKUS_HIBERNATE_SEARCH_ORM_ELASTICSEARCH_HOSTS},QUARKUS_HIBERNATE_SEARCH_ORM_ELASTICSEARCH_PROTOCOL=${QUARKUS_HIBERNATE_SEARCH_ORM_ELASTICSEARCH_PROTOCOL}

eb-deploy:
	@eb deploy ${ENV_NAME}

eb-terminate:
	@eb terminate ${ENV_NAME}

#Wrapper to build and deploy working dir code
deploy-wd:
	$(eval GIT_VERSION=$(shell git describe --tags --dirty)-$(shell git rev-parse --abbrev-ref HEAD)-$(shell date +%Y%m%d-%H%M%S))
# Build and push container
	$(MAKE) docker GIT_VERSION=${GIT_VERSION} RELEASE=${GIT_VERSION}
	$(MAKE) docker-push RELEASE=${GIT_VERSION}
# Create and deploy EB application version
	sed -i 's/\(AGR_CURATION_RELEASE: \).\+/\1'${GIT_VERSION}'/' .ebextensions/version.config
	zip -r ${GIT_VERSION}.zip docker-compose.yml .ebextensions/
	aws s3 cp ${GIT_VERSION}.zip s3://${EB_S3_BUCKET}/curation-app/
	aws elasticbeanstalk create-application-version --process --application-name curation-app --version-label ${GIT_VERSION} --source-bundle S3Bucket=${EB_S3_BUCKET},S3Key=curation-app/${GIT_VERSION}.zip
	eb deploy ${ENV_NAME} --version ${GIT_VERSION} -p
