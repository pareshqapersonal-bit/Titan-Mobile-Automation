pipeline {
    agent any

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
                    string(credentialsId: 'BROWSERSTACK_USERNAME', variable: 'BROWSERSTACK_USERNAME'),
                    string(credentialsId: 'BROWSERSTACK_ACCESS_KEY', variable: 'BROWSERSTACK_ACCESS_KEY'),
                    string(credentialsId: 'TEST_DEFAULT_USER_MOBILE', variable: 'TEST_DEFAULT_USER_MOBILE'),
                    string(credentialsId: 'TEST_DEFAULT_USER_PASSWORD', variable: 'TEST_DEFAULT_USER_PASSWORD')
                ]) {
                    bat """
                        mvn clean test -DsuiteXmlFile=%SUITE_FILE%
                    """
                }
            }
        }
    }
}