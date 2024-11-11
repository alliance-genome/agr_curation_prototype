# AGR Curation API

This repo holds the code for the API and the UI of the AGR curation system.

## Getting Started

These instructions will get you a copy of the project and the API up and running locally. This includes Postgres, OpenSearch and Cerebro.

## Contents

-  [Developing](#developing)
   *  [Branching](#branching)
-  [Installing](#installing)
   *  [Docker Setup](#docker-setup)
   *  [Postgres](#postgres)
   *  [OpenSearch](#opensearch-elastic-search-alternative)
   *  [Cerebro](#cerebro)
-  [Code Style](#codestyle)
   *  [Running Checkstyle](#running-checkstyle)
   *  [Checkstyle Links](#checkstyle-links)
-  [Building](#building)
   *  [Building Docker Image](#building-docker-image)
-  [Running](#running)
   *  [Running API](#running-api)
   *  [Running UI](#running-ui)
-  [Releasing and Deploying](#releasing-and-deploying)
   *  [Deployment environments](#deployment-environments)
   *  [Promoting code versions](#promoting-code-versions)
      -  [Promoting code from alpha to beta](#promoting-code-from-alpha-to-beta)
      -  [Promoting code from beta to production](#promoting-code-from-beta-to-production)
   *  [Additional deployment steps](#additional-deployment-steps)
      -  [Pre-deployment steps](#pre-deployment-steps)
      -  [Post-deployment steps](#post-deployment-steps)
   *  [Release versioning](#release-versioning)
   *  [Manual Release Creation](#manual-release-creation)
   *  [Database](#database)
-  [Loading Data](#loading-data)
-  [Submitting Data](#submitting-data)
-  [Querying Data](#querying-data)
   *  [Elastic Search](#search)
   *  [Database Search](#find)

## Developing
Before you start coding for a new feature or making a bugfix,
it is important to know the intended goal of your code:
*  Developing fixes, new features and major changes to be released in some future release (standard)
   => Must be developed and tested on alpha
*  Smaller (bug)fixes to a version currently being previewed on beta, in preparation of a release to production
   => Must be developed and tested on beta
*  Hotfixes, urgent bugfixes to the current production version which cannot wait on the next full release (bundled with other updates currently under development) to be deployed
   => Must be developed off the latest production release.

For more details about the intended use of and deployment to each of the environments, see [deployment environments](#deployment-environments).

### Branching
The three permanent branches in this repository represent the code to be deployed to their respective environments: `alpha`, `beta` and `production`.

*  To create code that must solely go on the alpha environment (most day-to-day development):

   -  Create a feature branch to work on that starts off alpha and have the name contain the ticket number and a short description of it.
      ```bash
      > git checkout alpha
      > git pull
      > git checkout -b feature/${JIRA-TICKET-NR}_short-description
      ```

   -  Do your coding and testing, and push the branch to github
      ```bash
      #Coding and testing here
      > git push origin feature/${JIRA-TICKET-NR}_short-description
      ```

   -  Once coding and testing completed, submit a pull request in github to merge back to alpha.  
      If this PR does not require deployment (because it only concerns files like the README, or to
      batch multiple fast-consecutive PRs into a single deployment), apply the `no-deploy` GitHub label
      to the PR (on creation) to skip automatic deployment. Otherwise, deployment to alpha will
      automatically trigger once the PR is approved and merged.

*  To make fixes to the version currently deployed on the beta environment:

   -  Create a new `release/vx.y.z-rca` off beta (replacing `x`, `y`, `z` and `a` as appropriate),
      with as version the current beta release candidate version number increased by one (`a+1`).
      ```bash
      > git checkout beta
      > git pull
      > git checkout -b release/vx.y.z-rca
      > git push -u origin release/vx.y.z-rca
      ```

      Alternatively, you can use a `betafix/*` branch instead of a `release/vx.y.z-rca`
      for changes that need merging in the beta code branch but no release creation or deployment
      (such as certain github actions changes to fix broken workflows). Use these only when creating a new release is not an option.

   -  If one or a few specific things need fixing then work directly on this release branch,
      otherwise create feature branches starting from this release branch as approriate
      (including the ticket number in the branch name) and create PRs to merge those back
      into the release branch.

   -  Once coding and testing completed, ensure you've pushed the latest changes
      and submit a pull request in github to merge the `release/vx.y.z-rca` branch back to beta.
      Before merging, do the [pre-deployment steps](#pre-deployment-steps) when appropriate.
      Then merge, submitting a short description of the changes as commit message body, to trigger release creation and deployment.
      
   -  Once deployment completed successfully, do the require [post-deployment steps](#post-deployment-steps).

*  To make fixes to the version currently deployed on the production environment:

   -  Create a new patch release branch `release/vx.y.z` off production (replacing `x`, `y`, `z` and `a` as appropriate),
      with as version the current production patch version number increased by one (`z+1`).
      ```bash
      > git checkout production
      > git pull
      > git checkout -b release/vx.y.z
      > git push -u origin release/vx.y.z
      ```

      Alternatively, you can use a prodfix branch instead of a `release/vx.y.z`
      for changes that need merging in the beta code branch but no release creation or deployment
      (such as certain github actions changes to fix broken workflows). Use these only when creating a new release is not an option.

   -  If one or a few specific things need fixing then work directly on this release branch,
      otherwise create feature branches starting from this release branch as approriate
      (including the ticket number in the branch name) and create PRs to merge those back
      into the release branch.

   -  Once coding and testing completed, ensure you've pushed the latest changes
      and submit a pull request in github to merge the `release/vx.y.z` branch back to production.
      Make sure all approriate testing has occured, as merging this PR will deploy directly to production.

      Before merging, do the [pre-deployment steps](#pre-deployment-steps) when appropriate.
      Then merge, submitting a short description of the changes as commit message body, to trigger release creation and deployment.

### Data structure changes
When making code changes which change any of the data structures, the supporting database schema needs to reflect these changes
to successfully be able to deploy and run this new code. [Hibernate](https://hibernate.org/) takes care of creating new tables and columns,
but changing existing columns, removing columns and moving or transforming data is not handled by hibernate, as this usually requires human judgment to correctly assess the required steps to maintain data integrity.
To automate and correctly replicate the deployment of these sort of data migrations accross environments, we use [flyway](https://flywaydb.org/).

Flyway works by manually defining and storing SQL files to handle the data migrations in [`src/main/resources/db/migration/`](src/main/resources/db/migration/), with a filename formatted as `v${VERSION}__${DESCRIPTION}.sql`.
 * `${DESCRIPTION}` can be a small free-form description of the changes made and is optional (along with the `__` separator)
 * `${VERSION}` must be defined. The agreed format for version is `x.y.z.a`, where `x.y.z` represents the curation application version on which this migration file will be applied (excluding any release-candidate portion of it), and the `.a` part represents an incremental number for all migrations applied to this release.
As an example, the first migration to be applied on top of `v0.4.0` (so while developing what could become `v0.5.0`) could be named `v0.4.0.1__updated-da-foreign-keys.sql`, the second one `v0.4.0.2__renamed-reference-field.sql` etc.


## Installing

Currently there are several scripts in the `docker` directory that can be used to start the various servers, locally if there is not a dev server available.

### Maven

Maven version 3.8.1 should be installed in order to compile all the code and libraries.

```bash
> brew install maven # Mac
> apt-get install maven # Linux ubuntu
> # Windows: Use WSL to run Ubuntu and run entire setup there
```

### Docker setup

Docker desktop can be downloaded via the following link: [Docker Download](https://www.docker.com/products/docker-desktop)

Once docker is setup and installed you will need to issue the following docker command to get a docker network created:

```bash
> docker network create curation
549127add...
```

### Postgres

[Run Postgres Script](docker/run_postgres) which will launch an empty postgres instance locally from the official postgres docker image.

If you are needing to connect to the production version of the database, use your personal postgres credentials that have been provided to you,
or contact a system admin to recover them when you lost them.

When you need admin access into the production database, the following command can be used to get the credentials
(but will only work once you have been setup with an AWS user and configured your local aws environment).

```bash
docker run --rm -it -v ~/.aws:/root/.aws amazon/aws-cli secretsmanager get-secret-value --secret-id curation-db-admin --region us-east-1 --query SecretString --output text
```
The output will look something like the following:

```bash
	{
		"username":"********",
		"password":"********",
		"host":"********",
		"engine":"postgres",
		"port":5432,
		"dbname":"curation",
		"dbInstanceIdentifier":"curation-db"
	}
```

### OpenSearch (Elastic Search alternative)

[Run OpenSearch script](docker/run_es) which will launch OpenSearch from the official OpenSearch docker image.

If you point a browser over to `http://localhost:9200` you should see something like the following:

```bash
	{
		"name": "8e063394038e",
		"cluster_name": "docker-cluster",
		"cluster_uuid": "M50gdkzjRFeDbu0XBZq85g",
		"version": {
			"number": "7.9.0",
			"build_flavor": "default",
			"build_type": "docker",
			"build_hash": "a479a2a7fce0389512d6a9361301708b92dff667",
			"build_date": "2020-08-11T21:36:48.204330Z",
			"build_snapshot": false,
			"lucene_version": "8.6.0",
			"minimum_wire_compatibility_version": "6.8.0",
			"minimum_index_compatibility_version": "6.0.0-beta1"
		},
		"tagline": "You Know, for Search"
	}
	
```

### Cerebro

[Run Cerebro Script](docker/run_cerebro) which will launch cerebro, an ES control plane, from the appropriate docker image.

Connect to this by browsing to `http://localhost:9000`, this is used to connect to the ES server, which can be accessed by
entering `http://opensearch:9200` (the internal docker address) in the Node address field, while opensearch is directly
accessible at `http://localhost:9200` on the local machine.

### Up and Running Servers

After running all the previous commands issue the docker command: `docker ps -a`

This will show all the services that are up and running.

```
CONTAINER ID   IMAGE                                   COMMAND                  CREATED          STATUS          PORTS                                                                                                                                                                          NAMES
4ee5641a9481   yannart/cerebro                         "./bin/cerebro"          26 minutes ago   Up 26 minutes   0.0.0.0:9000->9000/tcp, :::9000->9000/tcp                                                                         cerebro
7811ffe3c92d   opensearchproject/opensearch:1.2.4   "./opensearch-docker…"   24 minutes ago   Up 24 minutes   0.0.0.0:9200->9200/tcp, :::9200->9200/tcp, 9300/tcp, 0.0.0.0:9600->9600/tcp, :::9600->9600/tcp, 9650/tcp          opensearch
7de3cf028e9c   postgres:13                             "docker-entrypoint.s…"   3 days ago       Up 3 days       0.0.0.0:5432->5432/tcp, :::5432->5432/tcp                                                                         postgres
```

## Code Style

This repository uses checkstyle to validate formatting of code to a coding standard. [Checkstyle](https://checkstyle.sourceforge.io/) is a development tool to help programmers write Java code that adheres to a coding standard. It automates the process of checking Java code to spare humans of this boring (but important) task. This makes it ideal for projects that want to enforce a coding standard.

This repository also uses [Prettier](https://prettier.io/) to validate and update the formatting of code in the frontend codebase.

### Running Checkstyle

To run checkstyle locally in order to check your code before committing run the following:

```bash
> mvn checkstyle:check
```

Or the make target:

```bash
> make checkstyle
```

This will check all the style of the code and report any issues that it finds with the code. Here is an example output:

```bash
> make checkstyle
[INFO] Scanning for projects...
[INFO] Inspecting build with total of 1 modules...
[INFO] Installing Nexus Staging features:
[INFO]   ... total of 1 executions of maven-deploy-plugin replaced with nexus-staging-maven-plugin
[INFO] 
[INFO] ----------------< org.alliancegenome:agr_curation_api >-----------------
[INFO] Building AGR Curation :: API 0.0.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- checkstyle:3.3.1:check (default-cli) @ agr_curation_api ---
[INFO] There is 1 error reported by Checkstyle 10.17.0 with checkstyle.xml ruleset.
[ERROR] src/main/java/org/alliancegenome/curation_api/main/Main.java:[9] (regexp) RegexpSinglelineJava: Indent must use tab characters
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  6.107 s
[INFO] Finished at: 2024-05-29T05:23:33-04:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-checkstyle-plugin:3.3.1:check (default-cli) on project agr_curation_api: You have 1 Checkstyle violation. -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
make: *** [checkstyle] Error 1
```

Checkstyle can be configured via the checkstyle.xml file in the root of the project, this file can also be used by IDE's to keep code in line with the ruleset.

### Running Prettier

To validate formatting on JavaScript files locally with Prettier: 

```bash
> npx prettier --check **/*.js
```

Or the make target:

```bash
> make prettier-check
```

To update formatting on JavaScript files locally with Prettier: 

```bash
> npx prettier --write **/*.js
```

Or the make target:

```bash
> make prettier-write
```

Prettier can be configured via the .prettierrc.yaml file in the src/main/cliapp director of this project, this file can also be used by IDE's to keep code in line with the ruleset.

### Checkstyle Links

* Checkstyle for vscode: [https://marketplace.visualstudio.com/items?itemName=shengchen.vscode-checkstyle](https://marketplace.visualstudio.com/items?itemName=shengchen.vscode-checkstyle)
* Checkstyle for Intellij: [https://stackoverflow.com/questions/26955766/intellij-idea-checkstyle](https://stackoverflow.com/questions/26955766/intellij-idea-checkstyle)
* Checkstyle for Eclipse: [https://www.youtube.com/watch?v=H90QoBp2NSQ](https://www.youtube.com/watch?v=H90QoBp2NSQ) or [https://checkstyle.org/eclipse-cs/#!/](https://checkstyle.org/eclipse-cs/#!/)

### Prettier Links

* Prettier for vscode: [https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode)
* Prettier for Intellij: [https://www.jetbrains.com/help/idea/prettier.html](https://www.jetbrains.com/help/idea/prettier.html)

## Building

Before building locally, make sure and create a copy of the application.properties file

```bash
> cp src/main/resources/application.properties.defaults src/main/resources/application.properties
```

this way custom configuration changes can be used without having to commit them into the repo.

Both the UI or API can be run locally without needing a separate build step.
For instructions on how to do so, see [Running](#running-api).

Building the application uberjar is done as part of the Docker image creation,
instructions on how to use this process for local image building can be found [below](#building-docker-image).

### Building Docker Image

When needed, the following command can be issued to build a complete runnable application as a docker image:

```bash
> make docker
```

## Running

Depending on which part of the application you would like to run find the following commands:

### Running API

The API does not have to be build in order to run it in dev mode

```bash
> make apirun
mvn compile quarkus:dev
...
Listening for transport dt_socket at address: 5005
__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
...
```

This will run just the API and you can find it running at: [http://localhost:8080](http://localhost:8080), however if the API was built without the UI there will be nothing to see at that url. There is a swagger interface running at: [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui).  
**Windows note**: If you're running the curation software through WSL (on Windows), Java might serve this interface through IPv6, in which case `localhost:8080` will be accessible within WSL, but not from your windows browser. Instead, you can then access the API and the swagger interface in your browser at it's IPv6 counterpart: [http://[::1]:8080](http://[::1]:8080).

Following the prompts at the bottom of the screen after the server is up and running. 

```bash
--
Tests paused
Press [r] to resume testing, [o] Toggle test output, [h] for more options>
```
Some notable commands are `s` for restarting the server to pick up java changes. `j` change the debugging level of the server, `q` or ctrl-c in order to stop the server.

When running into issues when running/building the API, try a clean build by running `make clean` first.

### Running UI

To build and run the UI, issue the following command:

```bash
> make uirun
...
```

This will start the local UI and it will be running at: [http://localhost:3000](http://localhost:3000) this is in develop mode so any changes made to code will automatically restart the UI. The UI also proxies all `/api` requests over to the API. 

If need be the API_URL can be changed by setting it on the command line before running the UI:

```bash
> export API_URL=https://alpha-curation.alliancegenome.org  # to send proxied requests to the alpha server.
```

Additionally, there are two convenience commands that will proxy `/api` requests to either the alpha or beta environment

```bash
> make uirunalpha
```

```bash
> make uirunbeta
```

### Running the docker image

To run the complete application as the [locally built docker image](#building-docker-image), execute the following command:
```bash
> make docker-run
```

## Releasing and deploying
### Deployment environments
There are three environments to which code automatically gets deployed at different stages during development:
*  The alpha environment should be regarded as the developers environment, where active code development can be happening
   at any moment, and things are expected to break every now and then.
   This environment receives new deployments on every push made to the alpha branch.
*  The beta environment should be regarded as the testers environment, where (external) testers can have a first look at
   newly developed functionality before it is ready to be pushed to production. It is more stable than alpha,
   but is still subject to regular change as feedback is collected and the final kinks are being ironed out.
   This environment receives new deployments for every pre-release created on Github.
*  The production environment is the final stage of deployment, where users can rely on full-tested and ready-to-use
   features and functionality at any moment. This is the most stable and reliable environment.
   This environment receives new deployments for every full (stable) release created on Github.

All deployments are fully automated through Github actions, for which the configuration files can be found in the [`.github/workflows/` directory](.github/workflows/).
Deployments to the alpha environment happen automatically as code gets pushed to the alpha branch (after merging a PR), but for
deployments to beta and production, a small number of steps needs to be taken in order to create a release and trigger deployment,
which are described below.

### Promoting code versions
The general flow from coding to a production release goes something like this:
1. Coding and testing for most tickets happens on alpha (throughout the sprint)
2. When a stable state of alpha gets reached (usually at sprint review),
   this stable state gets promoted to beta as a release candidate, available
   for external user testing and testing against the latest production data
   and for testsubmissions of new production-ready data.
3. On approval from the external users, and in agreement with the product manager and the team,
   this release candidate gets promoted to a new stable release on production.

As the code goes through the different stages, it becomes more and more stable as it gets closer to production.

#### Promoting code from alpha to beta
1. Look at the [alpha-environment dashboard](https://alpha-curation.alliancegenome.org/#/)
    and ensure that for each Entity and Ontology, the number in the `Database * count`  column matches the number in the `Search index * count` column (ignore the values for `Disease Annotations` and `Literature References`). If there is a mismatch for any row,
    investigate what caused this and fix the issue first before continuing the deployment.  
    It may be that the scheduled mass reindexing failed (run every Sunday 00:00 UTC), in which case a successful mass-reindexing must first
    be performed on the alpha environment, before continuing the deployment to the beta environment.
2. Decide on the exact commit to promote.
   As active development constantly happens on the alpha branch, a stable point must be chosen to promote
   in order to keep the amount of bugs present in the (pre)release to be created to a minimum.
   Usually this means choosing the commit which was on alpha at the time of presenting at the past sprint review meeting.

   The commit history can be visualised by browsing the github repository's [network graph page](https://github.com/alliance-genome/agr_curation/network).
3. Decide on a proper release version number to be used for the new prerelease
   that will be created as a result of this promotion (see [Release versioning](#release-versioning)).  
   Generally speaking, for beta (pre)releases that means either
   *  Incrementing the release candidate version on the latest release candidate
      when it was decided the previous release candidate will not be promoted to a full release
      and the latest changes need to be added in order to be able to create a full release.
    or  
   *  Incrementing the `MAJOR` or `MINOR` release numbers as appropriate when
      the previous release candidate was (or will be) promoted to a full release
      and reset to rc1 for the next release.
   *  When choosing an alpha release make sure to include the Pull Requests that may have fixed bugs shortly after the sprint review, if they needed to be included in beta release.
4. Create a release/v`x`.`y`.`z`-rc`a` branch from the commit on the alpha branch
   that was chosen earlier to be promoted.
   ```bash
   git pull
   git checkout -b release/vx.y.z-rca <commit-sha>
   git push origin release/vx.y.z-rca
   ```
5. Create a pull request to merge this release branch into the beta branch.
   Do not merge yet and do not enable auto-merge.
6. Await PR approval, then do the necessary [pre-deployment steps](#pre-deployment-steps)
   to prepare the beta environment for a successful deployement.
7. In the above PR, click the `Merge pull request` button and 
      * Leave the autogenerated `Merge pull request #...` commit title
      * Provide a short description of the changes introduced in this release in the commit body textarea.
        When deploying code as demoed during sprint review, use `As sprint review MMM DDth YYYY`
        as the commit message body (e.g. `As sprint review Jan 19th 2022`).
   Then hit the `confirm merge` button. This will trigger a chain of github actions workflows which will
      a. Create a git tag (using the commit message body provided as tag message)
      b. Create a github release (prerelease when merged in beta).
      c. Build and deploy the application as a container to AWS
8. In addition to the above decribed actions, merging a release-branch PR in beta will also automatically create
   a mergeback PR to alpha to make the (pre)release tag reachable from alpha (and report the correct version number
   in the alpha UI and version API endpoint). Assign one or a few reviewers to this PR, and merge it on approval.

#### Promoting code from beta to production
1. Look at the [beta-environment dashboard](https://beta-curation.alliancegenome.org/#/)
    and ensure that for each Entity and Ontology, the number in the `Database * count`  column matches the number in the `Search index * count` column (ignore the values for `Disease Annotations` and `Literature References`). If there is a mismatch for any row,
    investigate what caused this and fix the issue first before continuing the deployment.  
    It may be that the scheduled mass reindexing failed (run every Sunday 00:00 UTC), in which case a successful mass-reindexing must first
    be performed on the beta environment, before continuing the deployment of new features to the beta environment.
2. Decide on a proper release version number to be used for the new release
   that will be created as a result of this promotion (see [Release versioning](#release-versioning)).  
   Generally speaking, for production (full) releases that means removing the release-candidate extension
   from the release number used by the latest release candidate, which will be promoted to a full release here.
3. Create a release/v`x`.`y`.`z` branch from beta, or use a specific commit from history
   to promote if a new release canidate for the next release was already added to beta.
   ```bash
   git pull
   git checkout -b release/vx.y.z beta
   ```
4. Update the [RELEASE-NOTES.md](RELEASE-NOTES.md) file to contain
   a section describing all (noteworthy) changes made since the last full release,
   and include JIRA ticket and/or PR references where possible. Commit after update.
5. Push the release branch to github
   ```bash
   git push origin release/vx.y.z
   ```
6. Create a pull request to merge this release branch into the production branch.
   Do not merge yet and do not enable auto-merge.
7. Await PR approval, then do the necessary [pre-deployment steps](#pre-deployment-steps)
   to prepare the production environment for a successful deployement.
8. In the above PR, click the `Merge pull request` button and 
      * Leave the autogenerated `Merge pull request #...` commit title
      * Provide a short description of the changes introduced in this release in the commit body textarea.
        When deploying code as demoed during sprint review, use `As sprint review MMM DDth YYYY`
        as the commit message body (e.g. `As sprint review Jan 19th 2022`).
   Then hit the `confirm merge` button. This will trigger a chain of github actions workflows which will
      a. Create a git tag (using the commit message body provided as tag message)
      b. Create a github release (full latest release when merged in production).
      c. Build and deploy the application as a container to AWS
9. In addition to the above decribed actions, merging a release-branch PR in production will also automatically create
   a mergeback PR to alpha to make the release tag reachable from alpha (and report the correct version number
   in the alpha UI and version API endpoint). Assign one or a few reviewers to this PR, and merge it on approval.

### Additional deployment steps
In order to successfully deploy to the beta or production environment, as few additional steps need to be taken
before and after merging into the respective branch and triggering deployment to ensure
the new version of the application can function in a consistent state upon and after deployment.

#### Pre-deployment steps
1. Compare the environment variables set in the Elastic Beanstalk environment between the environment you want to deploy to and from (e.g. compare curation-beta to curation-alpha for deployment to beta, or curation-production to curation-beta for deployment to production). This can be done through the [EB console](https://console.aws.amazon.com/elasticbeanstalk/home?region=us-east-1#/application/overview?applicationName=curation-app), or by using the `eb printenv` CLI. Scan for relevant change:
    *  New variables should be added to the environment to be deployed to, **before** initiating the deployment
    *  ENV-specific value changes should be ignored (for example, datasource host will be different for each)
    *  Other variable value changes should be propagated as appropriate, **before** initiating the deployment
    *  Removed variables should be cleaned up **after** successfull deployment
2. When wanting to deploy a prerelease to the beta environment, reset the beta postgres DB and roll down the latest production DB backup.  
   This must be done to catch any potentially problems that could be caused by new data available only on the production environment,
   before the code causing it would get deployed to the production environment.  
   The restore function automatically prevents users from writing to the database while it is being reloaded,
   by temporarily making the target database read-only and restoring in a separated database before renaming.
    1. For instructions on how to trigger the restore, see the [agr_db_backups README](https://github.com/alliance-genome/agr_db_backups#manual-invocation)
    2. After the restore completed, restart the beta environment app-server to re-apply all flyway migrations
       that were not yet present in the restored (production) database.
       ```bash
       > aws elasticbeanstalk restart-app-server --environment-name curation-beta
       ```
    3. Check the logs for errors after app-server restart, which could indicate a DB restore failure
        and troubleshoot accordingly if necessary to fix any errors.
3. Connect to the Environment's search domain and delete all indexes. A link to the Cerebro view into each environment's search indexes is available in the curation interface under `Developer Links` > `Search index UI (cerebro)` (VPN connection required).
   Alternatively, you can reach this UI manually by browsing to the [AGR Cerebro interface](http://cerebro.alliancegenome.org:9000) and entering the environment's domain endpoint manually. The domain endpoint URL can be found through the [Amazon OpenSearch console](https://us-east-1.console.aws.amazon.com/aos/home?region=us-east-1#opensearch/domains).

#### Post-deployment steps
1. Check the logs for the environment that you're releasing to and ensure the application started successfully
   and that all flyway migrations completed successfully. If errors occured, troubleshoot and fix accordingly.
    * If the application failed to start due to incompatible indexes (the indexes were not deleted before deployment),
      delete the indexes and restart the app-server by running
      ```bash
      > aws elasticbeanstalk restart-app-server --environment-name curation-<beta|production>
      ```
    * If any of the migrations failed, try restarting the application once more,
      or create a new release to fix the failing migration if the second restart did not resolve the issue:
      1. Create a new `release/v*` branch from the beta or production branch as appropriate.
          * Update to the next release candidate (`a` in `vx.y.z-rca`) for fixes to beta, using the beta branch as starting commit
          * Update to the next patch release (`z` in `vx.y.z`) for fixes to production, using the production branch as starting commit
      2. Fix the required migration files
      3. Create a PR to merge the new `release/v*` branch back into beta/production
      4. repeat the post-deployment steps for this new release (candidate).
2. Reindex all data types by calling the `system/reindexeverything` endpoint with default arguments (found in the
   System Endpoints section in the swagger UI) and follow-up through the log server to check for progress and errors.
3. Once reindexing completed, look at the dashboard page (where you deployed to)
    and ensure that for each Entity and Ontology, the number in the `Database * count`  column matches the number in the `Search index * count` column (ignore the values for `Disease Annotations` and `Literature References`). If there is a mismatch for any row,
    investigate what caused this and fix the issue first before continuing the deployment.
4. If code to support new ontologies was added, create the respective new data loads through the Data loads page and load the new file.
5. After completing all above steps successfully, return to the code promoting section to complete the last step(s) ([alpha to beta](#promoting-code-from-alpha-to-beta) or [beta to production](#promoting-code-from-beta-to-production))


### Release versioning
For our release versioning, we apply [Semantic Versioning](https://semver.org/) whereby

*  `MAJOR`.`MINOR`.`PATCH` is used for full releases
*  Release-candidate extensions are used for prereleases in the format `MAJOR`.`MINOR`.`PATCH`-rc`x`
   where x is an increment starting at one.
*  `PATCH` version upgrades should only be used for hotfixing bugs on the production environment
   (e.g. a bugfix applied to v0.1.0 becomes v0.1.1).

__Note:__ For the time being, the `MAJOR` release number is kept at 0 to indicate the early development phase,
until the product is ready for active usage by users external to the development team, in a stable
production environment.


### Manual Release Creation
Release creation is generally automated through github actions upon merging appropriately named `release/*` branches
into either the beta branch (for prereleases) or the production branch (for full releases).

To manually create a new (pre-)release and deploy to beta or production, do the following steps:

1. Ensure you're on the branch you intend to create a new release for and pull the latest code.
   ```bash
   git checkout beta
   git pull
   ```

2. Determine the approriate release number to be created (see [Release versioning](#release-versioning)).

3. Ensure you have a clean working directory before continuing, you can save any local changes for later using `git stash` if needed.

4. Tag the release and push the tag to github. Prefix the release nr with `v` as tagname,
   and provide a short description for the release. Beta releases should contain a reference to the
   date they were demoed at sprint review when applicable.
   ```bash
   # Tag name should be the release nr to be released, prefixed with v (eg. v0.2.0-rc1)
   # Tag message for beta releases can be something like 'As sprint review Jan 19th 2022'
   git tag -a vx.y.z-rca -m 'As sprint review MMM DDth YYYY'
   git push origin vx.y.z-rca
   ```

5. Check the [pre-deployment steps](#pre-deployment-steps) and perform them as appropriate.

6. Go to the [AGR curation release page](https://github.com/alliance-genome/agr_curation/releases) on github, create a new release by clicking the "Draft a new release" button at the top.
   * In the "Choose a tag" selection box, select the git tag you created in the previous step
   * Give the release a proper title including the sofware name and the release number ("AGR Curation `release tag-name`")
   * Let GitHub autogenerate a summary of all changes made in this release by clicking the "Auto-generate rease notes" button.
   *  **Ensure** the "Set as a pre-release" checkbox at the bottom is checked appropriately.
      *  **Checking** this box creates a prerelease, which only get deployed to the **beta** environment.
      *  Leaving the box **unchecked** (the default) creates a full release which gets deployed to the **production** environment.

7. Confirm all entered details are correct and publish the release.

Once published, github actions kicks in and the release will get deployed to the appropriate environments.
Completion of these deployments is reported in the #a-team-code slack channel. After receiving a successful deployment notification,
perform the appropriate [post-deployment steps](#post-deployment-steps).

### Database
The Curation application connects to the postgres DB using a user called `curation_app`, which is member of a role called `curation_admins`.
This role and user are used to ensure the database can be closed for all but admin users on initiating a DB restore,
such that no accidential writes can happen to the postgres database during the restore process to ensure data integrity.
When restoring to or creating a new postgres server, ensure the `curation_admins` role exists and has a member user called `curation_app`,
 to ensure the database can be restored with the correct ownerships, and give the `curation_admins` role all permissions to
 the curation database, the `public` schema, and all tables and sequences in it, and change the default privileges to allow
 the curation_admins role all permissions on all newly created tables and sequences in the `public` schema.

This can be achieved by connecting to the curation database using the admin (postgres) user (using `psql`)
and executing the following queries:  
```sql
-- Create the role
CREATE ROLE curation_admins;

-- Create user (change the password)
CREATE USER curation_app WITH PASSWORD '...';
-- Grant role to user
GRANT curation_admins TO curation_app;

-- Grant required privileges to group (role)
GRANT ALL ON DATABASE curation TO GROUP curation_admins;
GRANT ALL ON SCHEMA public TO GROUP curation_admins;
GRANT ALL ON ALL TABLES IN SCHEMA public TO GROUP curation_admins;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO GROUP curation_admins;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO GROUP curation_admins;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO GROUP curation_admins;
```

## Loading Data

Assuming the API is running on localhost, check the Swagger interface for the correct endpoint to post data to. Here is an example of loading the Gene BGI file from the Alliance.

```bash
> curl -vX POST http://localhost:8080/api/gene/bulk/bgifile -d @1.0.1.4_BGI_ZFIN_4.json --header "Content-Type: application/json"
```

Here is an example of loading Do ontology:

```bash
> curl -vX POST http://localhost:8080/api/doterm/bulk/owl -d @doid.owl --header "Content-Type: application/xml"
```

Example of loading Allele's

```bash
> curl -vX POST http://localhost:8080/api/allele/bulk/allelefile -d @1.0.1.4_ALLELE_MGI_0.json --header "Content-Type: application/json"
```

## Submitting Data

Here is an example using curl:

```bash
curl \
   -H "Authorization: Bearer 2C07D715..." \
   -X POST "https://${Curation_System}.alliancegenome.org/api/data/submit" \
   -F "LoadType_SubType=@/full/path/to/file1.json" \
   -F "LoadType_SubType=@/full/path/to/file2.json"
```

Valid values for LoadType, and SubType can be found in the examples below.

### Curation System

This value can be one of the following list.

| Name | Description |
| --- | --- |
| alpha-curation | Alpha Curation Site |
| beta-curation | Beta Curation Site |
| curation | Production Curation Site |

DQMs can find information regarding the LinkML version and data classes currently supported by the curation system at the following link:
https://${Curation_System}.alliancegenome.org/api/version

### API Access Token

This will be a key that is generated via logging into the curation website for the DQM's to use for uploading files.

### Load Type

Load type corresponds with the type of file that load needs.

| Name | Description |
| --- | --- |
| GENE | LinkML Gene |
| ALLELE | LinkML Allele |
| AGM | LinkML AGM |
| DISEASE_ANNOTATION | LinkML Disease Annotations |

### Sub Type

This is a grouping mechanism to group files together

| Name | Description |
| --- | --- |
| FB    | Fly Base |
| HUMAN | Human Supplied by RGD |
| MGI   | Mouse Genome Database |
| RGD   | Rat Genome Database |
| SGD   | Saccharomyces Genome Database |
| WB    | Worm Base |
| ZFIN  | Zebrafish Information Network |

### Including corresponding LinkML version in the JSON file submission header

The LinkML version for which the file is being submitted now needs to be added to the JSON file header, for example:

```json
{
  "linkml_version" : "v1.3.2",
  "disease_agm_ingest_set" : [ {
```

## Querying Data

### Search

Documentation to use the [API search endpoints](SEARCH.md).

### Find

Documentation to use the [API find endpoints](FIND.md).

## EB Deployment (beta)
This section is WIP.

CLI deployment to Elastic Beanstalk requires the EB CLI to be installed on your local machine.
Follow the [instructions provided by AWS](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/eb-cli3-install.html) to do so.
The EB CLI uses the same config files and environment variables as the standard AWS CLI to locate the appropriate authentication.

A EB cli configuration file is stored in the `.elasticbeanstalk` repo subdirectory. If you would encounter (local) problems running eb commands, remove this directory and reinitialise it by executing `make eb-init`.

All EB environment configurations are stored in the [`.ebextensions`](.ebextensions/) repo subdirectory.

Before attempting a sandbox environment creation, inspect the `eb-create` Makefile target
and export all variables defined in it with the appropriate values in your local shell environment
to allow storage of all required configurations in the EB environment configurations.

Then you can run `make eb-create` to create a new environment and `make eb-terminate` to terminate it.

## Maintainers

Current maintainers:

*  [Adam Gibson](https://github.com/adamgibs)
*  [Andrés Becerra Sandoval](https://github.com/abecerra)
*  [Christian Pich](https://github.com/cmpich)
*  [Jyothi Thota](https://github.com/jt15)
*  [Ketaki Thorat](https://github.com/kthorat-prog)
*  [Mark Quinton-Tulloch](https://github.com/markquintontulloch)
*  [Manuel Luypaert](https://github.com/mluypaert)
*  [Olin Blodgett](https://github.com/oblodgett)

