.PHONY: all build

all:
	npm install

run:
	npm start

pretty:
	npx prettier --write .

runalpha:
	export API_URL=https://alpha-curation.alliancegenome.org; make run; unset API_URL

build:
	npm run-script build

build-dev: build
	cp build/index.html ../resources/META-INF/resources
	cp build/favicon.ico ../resources/META-INF/resources
	rm -fr ../resources/META-INF/resources/assets
	rm -fr ../resources/META-INF/resources/static
	mv build/assets ../resources/META-INF/resources
	mv build/static ../resources/META-INF/resources
