pipeline {
    agent any

    environment {
        TOMCAT_HOME = 'D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53'
        DEPLOY_HOME = 'D:\\QuizDeployment'
        BACKEND_DIR = 'D:\\QuizDeployment\\backend'
        WAR_DIR     = 'D:\\QuizDeployment\\appzillon'
        SCRIPT_DIR  = 'D:\\QuizDeployment\\scripts'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'YOUR_GITHUB_REPOSITORY_URL'
            }
        }

        stage('Build Backend') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Stop Backend') {
            steps {
                bat '"%SCRIPT_DIR%\\stop-backend.bat"'
            }
        }

        stage('Deploy Backend') {
            steps {
                bat '''
                    if not exist "%BACKEND_DIR%" mkdir "%BACKEND_DIR%"

                    del /Q "%BACKEND_DIR%\\*.jar" 2>nul

                    copy /Y "target\\quizapp-0.0.1-SNAPSHOT.jar" "%BACKEND_DIR%\\"
                '''
            }
        }

        stage('Start Backend') {
            steps {
                bat '"%SCRIPT_DIR%\\start-backend.bat"'
            }
        }

        stage('Stop Tomcat') {
            steps {
                bat '''
                    "%TOMCAT_HOME%\\bin\\shutdown.bat"
                    timeout /t 5 /nobreak
                '''
            }
        }

        stage('Deploy Appzillon WARs') {
            steps {
                bat '''
                    del /Q "%TOMCAT_HOME%\\webapps\\AppzillonServer.war" 2>nul
                    del /Q "%TOMCAT_HOME%\\webapps\\quizapp.war" 2>nul

                    rmdir /S /Q "%TOMCAT_HOME%\\webapps\\AppzillonServer" 2>nul
                    rmdir /S /Q "%TOMCAT_HOME%\\webapps\\quizapp" 2>nul

                    copy /Y "%WAR_DIR%\\AppzillonServer.war" "%TOMCAT_HOME%\\webapps\\"
                    copy /Y "%WAR_DIR%\\quizapp.war" "%TOMCAT_HOME%\\webapps\\"
                '''
            }
        }

        stage('Start Tomcat') {
            steps {
                bat '''
                    "%TOMCAT_HOME%\\bin\\startup.bat"
                '''
            }
        }
    }
}
