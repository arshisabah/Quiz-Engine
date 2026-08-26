pipeline {
    agent any

    environment {
        TOMCAT_HOME = 'D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53'
        DEPLOY_HOME = 'D:\\Deployment'
        BACKEND_DIR = 'D:\\Deployment\\backend'
        WAR_DIR     = 'D:\\Deployment\\appzillon'
        SCRIPT_DIR  = 'D:\\Deployment\\scripts'
    }

    stages {

        stage('Build Backend') {
            steps {
                echo 'Building Spring Boot backend...'
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Stop Backend') {
            steps {
                echo 'Stopping existing backend...'
                bat '"%SCRIPT_DIR%\\stop-backend.bat"'
            }
        }

        stage('Deploy Backend') {
            steps {
                echo 'Deploying backend JAR...'

                bat '''
                    if not exist "%BACKEND_DIR%" mkdir "%BACKEND_DIR%"

                    del /Q "%BACKEND_DIR%\\*.jar" 2>nul

                    copy /Y "target\\quizapp-0.0.1-SNAPSHOT.jar" "%BACKEND_DIR%\\"
                '''
            }
        }

        stage('Start Backend') {
            steps {
                echo 'Starting backend on port 8082...'
                bat '"%SCRIPT_DIR%\\start-backend.bat"'
            }
        }

        stage('Stop Tomcat') {
            steps {
                echo 'Stopping Tomcat...'

                bat '''
                    "%TOMCAT_HOME%\\bin\\shutdown.bat"
                    timeout /t 5 /nobreak
                '''
            }
        }

        stage('Deploy Appzillon WARs') {
            steps {
                echo 'Deploying Appzillon WAR files...'

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
                echo 'Starting Tomcat...'

                bat '''
                    "%TOMCAT_HOME%\\bin\\startup.bat"
                '''
            }
        }
    }
}
