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

    post {

        always {

            emailext(

                subject: "Titan Mobile Automation | ${currentBuild.currentResult} | Build #${env.BUILD_NUMBER}",

                body: """

<html>

<body>

<h2>Titan Mobile Automation - Test Execution Report</h2>

<table border="1" cellpadding="6" cellspacing="0">

    <tr>
        <td><b>Build</b></td>
        <td>#${env.BUILD_NUMBER}</td>
    </tr>

    <tr>
        <td><b>Environment</b></td>
        <td>${params.ENVIRONMENT}</td>
    </tr>

    <tr>
        <td><b>Execution Mode</b></td>
        <td>${params.EXECUTION_MODE}</td>
    </tr>

    <tr>
        <td><b>Device</b></td>
        <td>${params.DEVICE}</td>
    </tr>

    <tr>
        <td><b>Test Suite</b></td>
        <td>${params.TEST_SUITE}</td>
    </tr>

    <tr>
        <td><b>Result</b></td>
        <td>${currentBuild.currentResult}</td>
    </tr>

</table>

<br>

<p>
The detailed Extent Report is attached to this email.
</p>

<p>
<a href="${env.BUILD_URL}">Open Jenkins Build</a>
</p>

</body>

</html>

                """,

                to: "Paresh.p@fortune4.in",

                mimeType: 'text/html',

                attachmentsPattern: 'Reports/*.html'

            )

        }

    }

}