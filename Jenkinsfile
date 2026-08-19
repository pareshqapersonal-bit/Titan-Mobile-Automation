pipeline {

    agent any

    tools {

        jdk 'JDK-21'

        maven 'Maven-3.9.16'
    }

    stages {

        stage('Checkout') {

            steps {

                checkout scm
            }
        }

        stage('Prepare Test Suite') {

            steps {

                script {

                    if (params.TEST_SUITE == 'purchase') {

                        env.SUITE_FILE = 'testng-purchase.xml'

                    } else {

                        env.SUITE_FILE = 'testng.xml'
                    }
                }
            }
        }

        stage('Run Tests') {

            steps {

                withCredentials([

                    string(
                        credentialsId: 'BROWSERSTACK_USERNAME',
                        variable: 'BROWSERSTACK_USERNAME'
                    ),

                    string(
                        credentialsId: 'BROWSERSTACK_ACCESS_KEY',
                        variable: 'BROWSERSTACK_ACCESS_KEY'
                    ),

                    string(
                        credentialsId: 'TEST_DEFAULT_USER_MOBILE',
                        variable: 'TEST_DEFAULT_USER_MOBILE'
                    ),

                    string(
                        credentialsId: 'TEST_DEFAULT_USER_PASSWORD',
                        variable: 'TEST_DEFAULT_USER_PASSWORD'
                    )

                ]) {

                    bat """
                        mvn clean test -DsuiteXmlFile=%SUITE_FILE%
                    """
                }
            }
        }
    }

    post {

        always {

            mail(
                to: 'Paresh.p@fortune4.in',

                subject: "Titan Mobile Automation | ${currentBuild.currentResult} | Build #${env.BUILD_NUMBER}",

                body: """
Titan Mobile Automation

Build: #${env.BUILD_NUMBER}

Environment: ${params.ENVIRONMENT}

Execution Mode: ${params.EXECUTION_MODE}

Device: ${params.DEVICE}

Test Suite: ${params.TEST_SUITE}

Result: ${currentBuild.currentResult}

Jenkins Build:
${env.BUILD_URL}
"""
            )
        }
    }
}