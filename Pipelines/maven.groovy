// env.BUILD_SYSTEM_BRANCH = BUILD_SYSTEM_BRANCH
// env.COMPILER = COMPILER
// env.TAG = TAG
// env.DB_NAME = DB_NAME
// env.REGION = UPDATE_REGION
// env.DELIVERY_QUARTER = DELIVERY_QUARTER
// env.PRODUCTION_RUN = PRODUCTION_RUN
// env.PASS_PARAMETER = PASS_PARAMETER
env.Job_Paramert = Job_Paramert
def staticConfig   = readYaml file: 'config/static.yaml'
def projectName = staticConfig['Project_name']
// if (env.BUILD_SYSTEM_BRANCH == "master") {
//     currentBuild.setDisplayName("${env.BUILD_NUMBER}-${env.REGION}")
// }
// else {
//     currentBuild.setDisplayName("${env.BUILD_NUMBER}-${env.BUILD_SYSTEM_BRANCH}-${env.REGION}")
// }
// timestamps {
//     node(env.TAG) {
//         try {
//         retry(3) {
//             git changelog: false,
//                 credentialsId: 'f2361e6b-b8e0-dead-beef-5eed5bc1a59f',
//                 poll: false,
//                 url: 'https://deveo-ext.it.here.com/COLLAB/projects/akela/repositories/git/build',
//                 branch: BUILD_SYSTEM_BRANCH
//         }
//         def regionsExclusions   = readYaml file: 'config/compiler_exclusions.yaml'
//         if (UPDATE_REGION in regionsExclusions) {
//             println "$UPDATE_REGION is whitelisted. It will be skipped.\nExiting..."
//             currentBuild.result = 'SUCCESS'
//             return
//         }
//             stage("Preparation") {
//                 sh("rake aws:wakeup:rds RDS_NAME=${DB_NAME} RDS_TYPE=prod")
//                 env.PREVIOUS_COMPILED_REGION_PATH = PREVIOUS_COMPILED_REGION_PATH                   
//                 sh("rake clean")
//                 sh("rake prepare:compile")
//                 defined_previous_compiled_region_path = sh returnStdout: true, script: "rake prepare:compile:get_previous PREVIOUS_COMPILED_REGION_PATH=$PREVIOUS_COMPILED_REGION_PATH"                 
//                 prev_region_found = ! defined_previous_compiled_region_path.tokenize('\n').contains('nothing')
//                 if (prev_region_found) {
//                     env.PREVIOUS_COMPILED_REGION_PATH = defined_previous_compiled_region_path.trim()
//                     println "Previous compilation of this region was found! PREV_IDS.NDS was placed into $env.PREVIOUS_COMPILED_REGION_PATH"
//                 } else {
//                     println 'Previous compilation of this region is not found!'
//                     println "Search script returned: $defined_previous_compiled_region_path"
//                 }
//             }
//             stage("Compilation") {
//                 sh("rake compile")
//             }
//             stage("Package") {
//                 sh("rake nds:package:region")
//                 sh("rake nds:publish:region")
//                 sh("rake nds:sync:logs")
//             }
//         }
//         catch(e) {
//             error("[FAILED] $e")
//         }
//         finally {
//             sh("rake nds:upload:logs:compiler")
//         }
//     }
// }

// def staticConfig   = readYaml file: 'config/static.yaml'
// println staticConfig
pipeline{
    agent any
     tools { 
        maven 'LocalMaven' 
    }
    // 1. SCM
    // 2. Build
    stages{
        stage('Print all Parameters'){
            steps{
                println "$env.Job_Paramert"
                println projectName
            }

        }
        stage('Checkout for build'){
            steps{
            // checkout([$class: 'GitSCM',branches: scm.branches,extensions: scm.extensions + [[$class: 'CleanCheckout']],userRemoteConfigs: scm.userRemoteConfigs])
            // checkout([$class: 'GitSCM',branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanCheckout']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'git-credentials', url: 'https://github.com/user/repo.git']]])
            checkout(
                [$class: 'GitSCM',branches: [[name: '*/master']],
                doGenerateSubmoduleConfigurations: false, 
                extensions: [[$class: 'CleanCheckout']], 
                submoduleCfg: [], 
                userRemoteConfigs: 
                [[credentialsId: '6659ddc7-0e5c-4509-a3bb-bf5b8f54b77a', url: 'git@github.com:amjagada/maven-project.git']]])
            }
        }

        stage('Build'){
            steps{
                // sh 'mvn clean install -X -U -Dmaven.test.skip=true'
                sh 'mvn clean package'
            }
            post{
                success{
                    echo 'Maven build success'
                }
                failure{
                    echo 'Maven build failed'
                }
            }
        }
    }
}
