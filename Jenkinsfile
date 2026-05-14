#!groovy

@Library('cib-pipeline-library') _

import de.cib.pipeline.library.Constants
import de.cib.pipeline.library.kubernetes.BuildPodCreator
import de.cib.pipeline.library.ConstantsInternal
import de.cib.pipeline.library.MavenProjectInformation
import groovy.transform.Field

@Field MavenProjectInformation mavenProjectInformation = null
@Field Map pipelineParams = [
    pom: ConstantsInternal.DEFAULT_MAVEN_POM_PATH,
    mvnContainerName: Constants.MAVEN_JDK_17_CONTAINER,
    uiParamPresets: [:],
    testMode: false
]

pipeline {
    agent {
        kubernetes {
            yaml BuildPodCreator.cibStandardPod()
                    .withContainerFromName(pipelineParams.mvnContainerName)
                    .asYaml()
            defaultContainer pipelineParams.mvnContainerName
        }
    }

    // Parameter that can be changed in the Jenkins UI
    parameters {
        booleanParam(
            name: 'BUILD',
            defaultValue: true,
            description: "  └─ 🔨 Build feature branch"
        )
        booleanParam(
            name: 'USE_PRIVATE_REPO',
            defaultValue: true,
            description: "  └─ 🧰 Use private local repository"
        )
        booleanParam(
            name: 'DEPLOY_TO_ARTIFACTS',
            defaultValue: false,
            description: '└─ 📦 Deploy to artifacts.cibseven.org'
        )
        booleanParam(
            name: 'DEPLOY_TO_MAVEN_CENTRAL',
            defaultValue: false,
            description: '└─ 📤 Deploy artifacts to Maven Central'
        )
    }

    environment {
        MAVEN_LOCAL_REPO = getLocalJobRepo(env.JOB_NAME, params.USE_PRIVATE_REPO)
        // set bigger timeout to get rid of the maven issue with file locks: https://github.com/apache/maven/issues/9049
        MAVEN_LOCAL_REPO_OPTS = "-Dmaven.repo.local=${env.MAVEN_LOCAL_REPO} -Daether.connector.basic.threads=4 -Daether.metadataResolver.threads=4 -Daether.syncContext.named.time=120"
        // set maven options only if params.USE_PRIVATE_REPO is true, otherwise use shared repo as usual
        MAVEN_OPTS = "${params.USE_PRIVATE_REPO == true ? env.MAVEN_LOCAL_REPO_OPTS : ""}"
    }

    options {
        buildDiscarder(
            logRotator(
                // number of build logs to keep
                numToKeepStr:'5',
                // history to keep in days
                daysToKeepStr: '15',
                // artifacts are kept for days
                artifactDaysToKeepStr: '15',
                // number of builds have their artifacts kept
                artifactNumToKeepStr: '5'
            )
        )
        // Stop build after 240 minutes
        timeout(time: 240, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('Print Settings & Checkout') {
            steps {
                script {
                    printSettings()

                    def pom = readMavenPom file: pipelineParams.pom

                    // for overlays often no groupId is set as the parent groupId is used
                    def groupId = pom.groupId
                    if (groupId == null) {
                        groupId = pom.parent.groupId
                        echo "parent groupId is used"
                    }

                    mavenProjectInformation = new MavenProjectInformation(groupId, pom.artifactId, pom.version, pom.name, pom.description)

                    echo "Build Project: ${mavenProjectInformation.groupId}:${mavenProjectInformation.artifactId}, ${mavenProjectInformation.name} with version ${mavenProjectInformation.version}"

                    // Avoid Git "dubious ownership" error in checked out repository. Needed in
                    // build containers with newer Git versions. Originates from Jenkins running
                    // pipeline as root but repository being owned by user 1000. For more, see
                    // https://stackoverflow.com/questions/72978485/git-submodule-update-failed-with-fatal-detected-dubious-ownership-in-repositor
                    sh "git config --global --add safe.directory \$(pwd)"
                }
                script {
                    // if use private repo then try to get a repo template from the pool if possible
                    if (params.USE_PRIVATE_REPO) {
                        moveMavenRepoFromPool("${env.MAVEN_LOCAL_REPO}")
                    }
                }
            }
        }

        stage('Build') {
            when {
                expression { params.BUILD }
            }
            steps {
                script {
                    withMaven(options: [junitPublisher(disabled: false), jacocoPublisher(disabled: false)]) {
                        sh "mvn -T4 -Dbuild.number=${BUILD_NUMBER} install"
                    }
                    if (!params.DEPLOY_TO_ARTIFACTS && !params.DEPLOY_TO_MAVEN_CENTRAL) {
                        junit allowEmptyResults: true, testResults: ConstantsInternal.MAVEN_TEST_RESULTS
                    }
                }
            }
        }

        stage('Deploy to artifacts.cibseven.org') {
            when {
                allOf {
                    expression { params.DEPLOY_TO_ARTIFACTS }
                    expression { !params.DEPLOY_TO_MAVEN_CENTRAL }
                }
            }
            steps {
                script {
                    withMaven(options: []) {
                        sh "mvn -T4 -U clean deploy"
                    }

                    junit allowEmptyResults: true, testResults: ConstantsInternal.MAVEN_TEST_RESULTS
                }
            }
        }

        stage('Deploy to Maven Central') {
            when {
                allOf {
                    expression { params.DEPLOY_TO_MAVEN_CENTRAL }
                    expression { mavenProjectInformation.version.endsWith("-SNAPSHOT") == false }
                }
            }
            steps {
                script {
                    withMaven(options: []) {
                        withCredentials([file(credentialsId: 'credential-cibseven-community-gpg-private-key', variable: 'GPG_KEY_FILE'), string(credentialsId: 'credential-cibseven-community-gpg-passphrase', variable: 'GPG_KEY_PASS')]) {
                            sh "gpg --batch --import ${GPG_KEY_FILE}"

                            def GPG_KEYNAME = sh(script: "gpg --list-keys --with-colons | grep pub | cut -d: -f5", returnStdout: true).trim()

                            sh """
                                mvn -T4 -U \
                                    -Dgpg.keyname="${GPG_KEYNAME}" \
                                    -Dgpg.passphrase="${GPG_KEY_PASS}" \
                                    clean deploy \
                                    -Psonatype-oss-release,release \
                                    -DskipExamples \
                                    -Dskip.cibseven.release="${!params.DEPLOY_TO_ARTIFACTS}"
                            """
                        }
                    }

                    junit allowEmptyResults: true, testResults: ConstantsInternal.MAVEN_TEST_RESULTS
                }
            }
        }
    }

    post {
        always {
            script {
                echo 'End of the build'
                
                // move back to the repo pool to reuse
                if (params.USE_PRIVATE_REPO) {
                    moveMavenRepoToPool("${env.MAVEN_LOCAL_REPO}")
                }
            }
        }

        success {
            script {
                echo '✅ Build successful'
                if (params.DEPLOY_TO_MAVEN_CENTRAL == true) {
                    notifyResult(
                        office365WebhookId: pipelineParams.office365WebhookId,
                        message: "Application was successfully released with version ${mavenProjectInformation.version}"
                    )
                }
            }
        }

        unstable {
            script {
                log.warning '⚠️ Build unstable'
            }
        }

        failure {
            script {
                log.warning '❌ Build failed'
                if (env.BRANCH_NAME == pipelineParams.primaryBranch) {
                    notifyResult(
                        office365WebhookId: pipelineParams.office365WebhookId,
                        message: "❌ Build failed on main branch. Access build info at ${env.BUILD_URL}"
                    )
                }
            }
        }

        fixed {
            script {
                echo '✅ Previous issues fixed'
                if (env.BRANCH_NAME == pipelineParams.primaryBranch) {
                    notifyResult(
                        office365WebhookId: pipelineParams.office365WebhookId,
                        message: "✅ Previous issues on main branch fixed. Access build info at ${env.BUILD_URL}"
                    )
                }
            }
        }
    }
}

// creates pseudo-unique folder for a jobName
def getJobFolder(String jobName) {
    String baseName = jobName.split('/') ? jobName.split('/').last() : ""
    String pathHash = String.format("%04X", (0xFFFF & jobName.hashCode()))
    return "${baseName}_${pathHash}".replaceAll("[^a-zA-Z0-9._-]", "_")
}

def moveMavenRepoFromPool(String localRepoPath) {
    // Try to find and move from the pool: preferably a folder with the same name as the local repo, otherwise the oldest existing folder
    echo "Find a suitable template in the pool and move it to the local repo path: '${localRepoPath}'"
    if (localRepoPath != null && localRepoPath.endsWith(getJobFolder("${JOB_NAME}"))) {
        String baseDir = localRepoPath.split('/').last()
        String poolDir = localRepoPath.substring(0, localRepoPath.lastIndexOf('/')) + "/.pool"
        String poolItemOwn = poolDir + "/" + baseDir
        String poolItemAny = sh(script: "find ${poolDir} -maxdepth 1 -mindepth 1 -type d -printf '%T+\\t%p\\n' | sort | head -n 1 | cut -f2-", returnStdout: true).trim()
        // use already existing repo or move a repo template from the pool, prefer own, fail if not successful: don't fall to the long-time repo creation
        if (poolItemAny != null && !poolItemAny.isEmpty()) {
            sh "[ -e '${localRepoPath}' ] || mv -v '${poolItemOwn}' '${localRepoPath}' || mv -v '${poolItemAny}' '${localRepoPath}'"
            sh "touch -c '${localRepoPath}'"
        }
        else {
            echo "WARNING: no suitable repo is found in the pool: will create a new one from scratch. A very long build is expected !!!"
        }
    }
}

def moveMavenRepoToPool(String localRepoPath) {
    echo "Move local repo '${localRepoPath}' back to the pool or delete it if the movement fails"
    if (localRepoPath != null && localRepoPath.endsWith(getJobFolder("${JOB_NAME}"))) {
        String poolDir = localRepoPath.substring(0, localRepoPath.lastIndexOf('/')) + "/.pool"
        sh "[ -e '${localRepoPath}' ] && rm -rf '${localRepoPath}/org/cibseven' && mv -v '${localRepoPath}' '${poolDir}/' || rm -rf '${localRepoPath}'"
    }
}
