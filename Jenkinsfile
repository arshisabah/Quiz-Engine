pipeline {

    agent any

    environment {

        // =========================================================
        // GITHUB BACKEND REPOSITORY
        // =========================================================

        BACKEND_REPO = 'https://github.com/arshisabah/Quiz-Engine.git'


        // =========================================================
        // LOCAL DEPLOYMENT DIRECTORY
        // =========================================================

        DEPLOYMENT_DIR = 'D:\\Deployment'

        BACKEND_DIR = 'D:\\Deployment\\backend'

        APPZILLON_DIR = 'D:\\Deployment\\appzillon'


        // =========================================================
        // APPZILLON WAR FILES
        // =========================================================

        APPZILLON_SERVER_WAR =
            'D:\\Deployment\\appzillon\\AppzillonServer.war'

        QUIZAPP_WAR =
            'D:\\Deployment\\appzillon\\quizapp.war'


        // =========================================================
        // BACKEND
        // =========================================================

        BACKEND_JAR =
            'D:\\Deployment\\backend\\quizapp-0.0.1-SNAPSHOT.jar'

        BACKEND_LOG =
            'D:\\Deployment\\backend\\backend.log'

        BACKEND_ERROR_LOG =
            'D:\\Deployment\\backend\\backend-error.log'

        BACKEND_PID =
            'D:\\Deployment\\backend\\backend.pid'


        // =========================================================
        // TOMCAT
        // =========================================================

        TOMCAT_HOME =
            'D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53'

        TOMCAT_WEBAPPS =
            'D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53\\webapps'


        // =========================================================
        // APPLICATION PORTS
        // =========================================================

        BACKEND_PORT = '8082'

        TOMCAT_PORT = '8080'
    }


    stages {


        // =========================================================
        // 1. CHECKOUT BACKEND
        // =========================================================

        stage('Checkout Backend') {

            steps {

                echo '========================================'
                echo 'Checking out Quiz Engine backend'
                echo '========================================'

                checkout([
                    $class: 'GitSCM',

                    branches: [[
                        name: '*/main'
                    ]],

                    userRemoteConfigs: [[
                        url: "${BACKEND_REPO}"
                    ]]
                ])
            }
        }


        // =========================================================
        // 2. CHECK ENVIRONMENT
        // =========================================================

        stage('Check Environment') {

            steps {

                echo '========================================'
                echo 'Checking Java'
                echo '========================================'

                bat '''
                    java -version
                '''


                echo '========================================'
                echo 'Checking Maven'
                echo '========================================'

                bat '''
                    mvn -version
                '''


                echo '========================================'
                echo 'Checking deployment files'
                echo '========================================'

                bat '''
                    if not exist "%APPZILLON_SERVER_WAR%" (
                        echo ERROR: AppzillonServer.war not found
                        exit /b 1
                    )

                    if not exist "%QUIZAPP_WAR%" (
                        echo ERROR: quizapp.war not found
                        exit /b 1
                    )

                    if not exist "%TOMCAT_HOME%" (
                        echo ERROR: Tomcat not found
                        exit /b 1
                    )

                    echo AppzillonServer.war - OK
                    echo quizapp.war - OK
                    echo Tomcat - OK
                '''
            }
        }


        // =========================================================
        // 3. BUILD BACKEND
        // =========================================================

        stage('Build Backend') {

            steps {

                echo '========================================'
                echo 'Building Spring Boot Backend'
                echo '========================================'

                bat '''
                    mvn clean package -DskipTests
                '''
            }
        }


        // =========================================================
        // 4. COPY BACKEND JAR
        // =========================================================

        stage('Copy Backend JAR') {

            steps {

                echo '========================================'
                echo 'Copying backend JAR'
                echo '========================================'

                bat '''
                    if not exist "%BACKEND_DIR%" (
                        mkdir "%BACKEND_DIR%"
                    )

                    if not exist "target\\quizapp-0.0.1-SNAPSHOT.jar" (
                        echo ERROR: JAR was not generated
                        exit /b 1
                    )

                    copy /Y ^
                    "target\\quizapp-0.0.1-SNAPSHOT.jar" ^
                    "%BACKEND_JAR%"

                    echo Backend JAR copied successfully.
                '''
            }
        }


        // =========================================================
        // 5. STOP OLD BACKEND
        // =========================================================

        stage('Stop Backend') {

            steps {

                echo '========================================'
                echo 'Stopping existing backend'
                echo '========================================'

                bat '''
                    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
                    "$processes = Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like '*quizapp-0.0.1-SNAPSHOT.jar*' }; ^
                    foreach ($p in $processes) { ^
                        Write-Host ('Stopping backend PID: ' + $p.ProcessId); ^
                        Stop-Process -Id $p.ProcessId -Force ^
                    }"
                '''

                timeout(time: 15, unit: 'SECONDS') {

                    bat '''
                        powershell -NoProfile -Command ^
                        "Start-Sleep -Seconds 3"
                    '''
                }
            }
        }


        // =========================================================
        // 6. START BACKEND
        // =========================================================

        stage('Start Backend') {

            steps {

                echo '========================================'
                echo 'Starting Spring Boot Backend'
                echo '========================================'

                bat '''
                    if exist "%BACKEND_LOG%" (
                        del /F /Q "%BACKEND_LOG%"
                    )

                    if exist "%BACKEND_ERROR_LOG%" (
                        del /F /Q "%BACKEND_ERROR_LOG%"
                    )

                    if exist "%BACKEND_PID%" (
                        del /F /Q "%BACKEND_PID%"
                    )


                    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
                    "$p = Start-Process ^
                    -FilePath 'java' ^
                    -ArgumentList '-jar','%BACKEND_JAR%' ^
                    -RedirectStandardOutput '%BACKEND_LOG%' ^
                    -RedirectStandardError '%BACKEND_ERROR_LOG%' ^
                    -PassThru; ^
                    Set-Content '%BACKEND_PID%' $p.Id; ^
                    Write-Host ('Backend started. PID = ' + $p.Id)"
                '''
            }
        }


        // =========================================================
        // 7. WAIT FOR BACKEND
        // =========================================================

        stage('Wait For Backend') {

            steps {

                echo '========================================'
                echo 'Waiting for backend on port 8082'
                echo '========================================'

                timeout(time: 90, unit: 'SECONDS') {

                    bat '''
                        powershell -NoProfile -ExecutionPolicy Bypass -Command ^
                        "$ready = $false; ^
                        for ($i = 0; $i -lt 18; $i++) { ^
                            try { ^
                                $connection = Test-NetConnection ^
                                    -ComputerName 'localhost' ^
                                    -Port 8082 ^
                                    -WarningAction SilentlyContinue; ^

                                if ($connection.TcpTestSucceeded) { ^
                                    Write-Host 'Backend is UP on port 8082'; ^
                                    $ready = $true; ^
                                    break ^
                                } ^
                            } ^
                            catch {} ^

                            Write-Host 'Backend is not ready yet...'; ^
                            Start-Sleep -Seconds 5 ^
                        }; ^

                        if (-not $ready) { ^
                            Write-Host 'Backend failed to start.'; ^
                            exit 1 ^
                        }"
                    '''
                }
            }
        }


        // =========================================================
        // 8. STOP TOMCAT
        // =========================================================

        stage('Stop Tomcat') {

            steps {

                echo '========================================'
                echo 'Stopping Tomcat'
                echo '========================================'

                bat '''
                    call "%TOMCAT_HOME%\\bin\\shutdown.bat"

                    timeout /t 10 /nobreak
                '''
            }
        }


        // =========================================================
        // 9. CLEAN OLD APPZILLON DEPLOYMENT
        // =========================================================

        stage('Clean Tomcat') {

            steps {

                echo '========================================'
                echo 'Cleaning old Appzillon deployment'
                echo '========================================'

                bat '''
                    if exist "%TOMCAT_WEBAPPS%\\AppzillonServer.war" (
                        del /F /Q "%TOMCAT_WEBAPPS%\\AppzillonServer.war"
                    )

                    if exist "%TOMCAT_WEBAPPS%\\quizapp.war" (
                        del /F /Q "%TOMCAT_WEBAPPS%\\quizapp.war"
                    )


                    if exist "%TOMCAT_WEBAPPS%\\AppzillonServer" (
                        rmdir /S /Q "%TOMCAT_WEBAPPS%\\AppzillonServer"
                    )

                    if exist "%TOMCAT_WEBAPPS%\\quizapp" (
                        rmdir /S /Q "%TOMCAT_WEBAPPS%\\quizapp"
                    )


                    echo Tomcat deployment directory cleaned.
                '''
            }
        }


        // =========================================================
        // 10. DEPLOY APPZILLON
        // =========================================================

        stage('Deploy Appzillon') {

            steps {

                echo '========================================'
                echo 'Deploying Appzillon WAR files'
                echo '========================================'

                bat '''
                    echo Copying AppzillonServer.war...

                    copy /Y ^
                    "%APPZILLON_SERVER_WAR%" ^
                    "%TOMCAT_WEBAPPS%\\AppzillonServer.war"


                    echo Copying quizapp.war...

                    copy /Y ^
                    "%QUIZAPP_WAR%" ^
                    "%TOMCAT_WEBAPPS%\\quizapp.war"


                    echo Appzillon WAR files deployed.
                '''
            }
        }


        // =========================================================
        // 11. START TOMCAT
        // =========================================================

        stage('Start Tomcat') {

            steps {

                echo '========================================'
                echo 'Starting Tomcat'
                echo '========================================'

                bat '''
                    call "%TOMCAT_HOME%\\bin\\startup.bat"

                    timeout /t 20 /nobreak
                '''
            }
        }


        // =========================================================
        // 12. WAIT FOR TOMCAT
        // =========================================================

        stage('Wait For Tomcat') {

            steps {

                echo '========================================'
                echo 'Waiting for Tomcat'
                echo '========================================'

                timeout(time: 90, unit: 'SECONDS') {

                    bat '''
                        powershell -NoProfile -ExecutionPolicy Bypass -Command ^
                        "$ready = $false; ^
                        for ($i = 0; $i -lt 18; $i++) { ^
                            try { ^
                                $connection = Test-NetConnection ^
                                    -ComputerName 'localhost' ^
                                    -Port 8080 ^
                                    -WarningAction SilentlyContinue; ^

                                if ($connection.TcpTestSucceeded) { ^
                                    Write-Host 'Tomcat is UP on port 8080'; ^
                                    $ready = $true; ^
                                    break ^
                                } ^
                            } ^
                            catch {} ^

                            Write-Host 'Tomcat is not ready yet...'; ^
                            Start-Sleep -Seconds 5 ^
                        }; ^

                        if (-not $ready) { ^
                            Write-Host 'Tomcat failed to start.'; ^
                            exit 1 ^
                        }"
                    '''
                }
            }
        }


        // =========================================================
        // 13. VERIFY DEPLOYMENT
        // =========================================================

        stage('Verify Deployment') {

            steps {

                echo '========================================'
                echo 'Verifying Deployment'
                echo '========================================'

                bat '''
                    echo.
                    echo Checking Backend...
                    powershell -NoProfile -Command ^
                    "Test-NetConnection localhost -Port 8082"

                    echo.
                    echo Checking Tomcat...
                    powershell -NoProfile -Command ^
                    "Test-NetConnection localhost -Port 8080"


                    echo.
                    echo Checking WAR files...

                    if not exist "%TOMCAT_WEBAPPS%\\AppzillonServer.war" (
                        echo ERROR: AppzillonServer.war missing
                        exit /b 1
                    )

                    if not exist "%TOMCAT_WEBAPPS%\\quizapp.war" (
                        echo ERROR: quizapp.war missing
                        exit /b 1
                    )


                    echo.
                    echo ========================================
                    echo BACKEND + APPZILLON DEPLOYMENT SUCCESS
                    echo ========================================
                '''
            }
        }
    }


    // =============================================================
    // POST ACTIONS
    // =============================================================

    post {

        success {

            echo '''
            ================================================
                 DEPLOYMENT SUCCESSFUL
            ================================================

            Backend:
            http://localhost:8082

            Tomcat:
            http://localhost:8080

            Appzillon:
            http://localhost:8080/quizapp

            ================================================
            '''
        }


        failure {

            echo '''
            ================================================
                 DEPLOYMENT FAILED
            ================================================
            '''


            bat '''
                echo.
                echo ========================================
                echo BACKEND LOG
                echo ========================================

                if exist "%BACKEND_LOG%" (
                    type "%BACKEND_LOG%"
                )


                echo.
                echo ========================================
                echo BACKEND ERROR LOG
                echo ========================================

                if exist "%BACKEND_ERROR_LOG%" (
                    type "%BACKEND_ERROR_LOG%"
                )


                echo.
                echo ========================================
                echo TOMCAT LOG LOCATION
                echo ========================================

                echo %TOMCAT_HOME%\\logs
            '''
        }
    }
}