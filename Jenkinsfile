pipeline {

    agent any

    environment {

        // =====================================================
        // DEPLOYMENT DIRECTORIES
        // =====================================================

        DEPLOYMENT_DIR = 'D:\\Deployment'

        BACKEND_DIR = 'D:\\Deployment\\backend'

        APPZILLON_DIR = 'D:\\Deployment\\appzillon'


        // =====================================================
        // APPZILLON WAR FILES
        // =====================================================

        APPZILLON_SERVER_WAR =
            'D:\\Deployment\\appzillon\\AppzillonServer.war'

        QUIZAPP_WAR =
            'D:\\Deployment\\appzillon\\quizapp.war'


        // =====================================================
        // BACKEND
        // =====================================================

        BACKEND_JAR =
            'D:\\Deployment\\backend\\quizapp-0.0.1-SNAPSHOT.jar'

        BACKEND_LOG =
            'D:\\Deployment\\backend\\backend.log'

        BACKEND_ERROR_LOG =
            'D:\\Deployment\\backend\\backend-error.log'

        BACKEND_PID =
            'D:\\Deployment\\backend\\backend.pid'


        // =====================================================
        // TOMCAT
        // =====================================================

        TOMCAT_HOME =
            'D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53'

        TOMCAT_WEBAPPS =
            'D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53\\webapps'


        // =====================================================
        // PORTS
        // =====================================================

        BACKEND_PORT = '8082'

        TOMCAT_PORT = '8084'
    }


    stages {


        // =====================================================
        // 1. CHECKOUT
        // =====================================================

        stage('Checkout') {

            steps {

                echo '============================================'
                echo 'CHECKOUT BACKEND'
                echo '============================================'

                git branch: 'main',
                    url: 'https://github.com/arshisabah/Quiz-Engine.git'
            }
        }


        // =====================================================
        // 2. CHECK ENVIRONMENT
        // =====================================================

        stage('Check Environment') {

            steps {

                echo '============================================'
                echo 'CHECKING ENVIRONMENT'
                echo '============================================'

                bat '''
                    echo.
                    echo ===== JAVA =====
                    java -version

                    echo.
                    echo ===== MAVEN =====
                    mvn -version

                    echo.
                    echo ===== CHECKING TOMCAT =====

                    if not exist "%TOMCAT_HOME%" (
                        echo ERROR: Tomcat not found
                        exit /b 1
                    )

                    echo Tomcat found:
                    echo %TOMCAT_HOME%


                    echo.
                    echo ===== CHECKING APPZILLON SERVER WAR =====

                    if not exist "%APPZILLON_SERVER_WAR%" (
                        echo ERROR: AppzillonServer.war not found
                        exit /b 1
                    )

                    echo AppzillonServer.war found


                    echo.
                    echo ===== CHECKING QUIZAPP WAR =====

                    if not exist "%QUIZAPP_WAR%" (
                        echo ERROR: quizapp.war not found
                        exit /b 1
                    )

                    echo quizapp.war found
                '''
            }
        }


        // =====================================================
        // 3. BUILD BACKEND
        // =====================================================

        stage('Build Backend') {

            steps {

                echo '============================================'
                echo 'BUILDING SPRING BOOT BACKEND'
                echo '============================================'

                bat '''
                    mvn clean package -DskipTests
                '''
            }
        }


        // =====================================================
        // 4. COPY BACKEND JAR
        // =====================================================

        stage('Copy Backend JAR') {

            steps {

                echo '============================================'
                echo 'COPYING BACKEND JAR'
                echo '============================================'

                bat '''
                    if not exist "%BACKEND_DIR%" (
                        mkdir "%BACKEND_DIR%"
                    )

                    if not exist "target\\quizapp-0.0.1-SNAPSHOT.jar" (
                        echo ERROR: Backend JAR was not created
                        exit /b 1
                    )

                    copy /Y ^
                    "target\\quizapp-0.0.1-SNAPSHOT.jar" ^
                    "%BACKEND_JAR%"

                    echo.
                    echo Backend JAR copied successfully.
                '''
            }
        }


        // =====================================================
        // 5. STOP OLD BACKEND
        // =====================================================

        stage('Stop Backend') {
        
            steps {
        
                echo '============================================'
                echo 'STOPPING OLD BACKEND'
                echo '============================================'
        
                bat '''
                    powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like '*quizapp-0.0.1-SNAPSHOT.jar*' } | ForEach-Object { Write-Host ('Stopping backend PID: ' + $_.ProcessId); Stop-Process -Id $_.ProcessId -Force }"
        
                    timeout /t 3 /nobreak
                '''
            }
        }


        // =====================================================
        // 6. START BACKEND
        // =====================================================

        stage('Start Backend') {
        
            steps {
        
                echo '============================================'
                echo 'STARTING BACKEND'
                echo '============================================'
        
                bat '''
                    if exist "%BACKEND_LOG%" del /F /Q "%BACKEND_LOG%"
                    if exist "%BACKEND_ERROR_LOG%" del /F /Q "%BACKEND_ERROR_LOG%"
                    if exist "%BACKEND_PID%" del /F /Q "%BACKEND_PID%"
        
                    powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "$p = Start-Process -FilePath 'java' -ArgumentList '-jar','%BACKEND_JAR%' -WorkingDirectory '%BACKEND_DIR%' -RedirectStandardOutput '%BACKEND_LOG%' -RedirectStandardError '%BACKEND_ERROR_LOG%' -PassThru; Set-Content '%BACKEND_PID%' $p.Id; Write-Host ('Backend started with PID: ' + $p.Id)"
                '''
            }
        }


        // =====================================================
        // 7. WAIT FOR BACKEND
        // =====================================================

        stage('Wait For Backend') {

            steps {

                echo '============================================'
                echo 'WAITING FOR BACKEND :8082'
                echo '============================================'

                timeout(time: 90, unit: 'SECONDS') {

                    bat '''
                        powershell -NoProfile -ExecutionPolicy Bypass -Command ^
                        "$ready = $false; ^
                        for ($i = 0; $i -lt 18; $i++) { ^

                            $result = Test-NetConnection ^
                                -ComputerName 'localhost' ^
                                -Port 8082 ^
                                -WarningAction SilentlyContinue; ^

                            if ($result.TcpTestSucceeded) { ^
                                Write-Host '================================'; ^
                                Write-Host 'BACKEND IS UP ON PORT 8082'; ^
                                Write-Host '================================'; ^
                                $ready = $true; ^
                                break ^
                            ^

                            Write-Host 'Backend not ready... waiting 5 seconds'; ^
                            Start-Sleep -Seconds 5 ^
                        } ^

                        if (-not $ready) { ^
                            Write-Host 'Backend failed to start'; ^
                            exit 1 ^
                        }"
                    '''
                }
            }
        }


        // =====================================================
        // 8. STOP TOMCAT
        // =====================================================

        stage('Stop Tomcat') {

            steps {

                echo '============================================'
                echo 'STOPPING TOMCAT'
                echo '============================================'

                bat '''
                    call "%TOMCAT_HOME%\\bin\\shutdown.bat"

                    echo Waiting for Tomcat to stop...

                    timeout /t 10 /nobreak
                '''
            }
        }


        // =====================================================
        // 9. CLEAN TOMCAT
        // =====================================================

        stage('Clean Tomcat') {

            steps {

                echo '============================================'
                echo 'CLEANING TOMCAT DEPLOYMENT'
                echo '============================================'

                bat '''
                    echo Removing AppzillonServer.war...

                    if exist "%TOMCAT_WEBAPPS%\\AppzillonServer.war" (
                        del /F /Q "%TOMCAT_WEBAPPS%\\AppzillonServer.war"
                    )


                    echo Removing quizapp.war...

                    if exist "%TOMCAT_WEBAPPS%\\quizapp.war" (
                        del /F /Q "%TOMCAT_WEBAPPS%\\quizapp.war"
                    )


                    echo Removing old AppzillonServer directory...

                    if exist "%TOMCAT_WEBAPPS%\\AppzillonServer" (
                        rmdir /S /Q "%TOMCAT_WEBAPPS%\\AppzillonServer"
                    )


                    echo Removing old quizapp directory...

                    if exist "%TOMCAT_WEBAPPS%\\quizapp" (
                        rmdir /S /Q "%TOMCAT_WEBAPPS%\\quizapp"
                    )


                    echo Tomcat cleaned.
                '''
            }
        }


        // =====================================================
        // 10. DEPLOY APPZILLON
        // =====================================================

        stage('Deploy Appzillon') {

            steps {

                echo '============================================'
                echo 'DEPLOYING APPZILLON'
                echo '============================================'

                bat '''
                    echo.
                    echo Copying AppzillonServer.war...

                    copy /Y ^
                    "%APPZILLON_SERVER_WAR%" ^
                    "%TOMCAT_WEBAPPS%\\AppzillonServer.war"


                    echo.
                    echo Copying quizapp.war...

                    copy /Y ^
                    "%QUIZAPP_WAR%" ^
                    "%TOMCAT_WEBAPPS%\\quizapp.war"


                    echo.
                    echo Appzillon WAR deployment completed.
                '''
            }
        }


        // =====================================================
        // 11. START TOMCAT
        // =====================================================

        stage('Start Tomcat') {

            steps {

                echo '============================================'
                echo 'STARTING TOMCAT :8084'
                echo '============================================'

                bat '''
                    call "%TOMCAT_HOME%\\bin\\startup.bat"

                    echo Waiting for Tomcat...

                    timeout /t 20 /nobreak
                '''
            }
        }


        // =====================================================
        // 12. WAIT FOR TOMCAT
        // =====================================================

        stage('Wait For Tomcat') {

            steps {

                echo '============================================'
                echo 'WAITING FOR TOMCAT :8084'
                echo '============================================'

                timeout(time: 90, unit: 'SECONDS') {

                    bat '''
                        powershell -NoProfile -ExecutionPolicy Bypass -Command ^
                        "$ready = $false; ^
                        for ($i = 0; $i -lt 18; $i++) { ^

                            $result = Test-NetConnection ^
                                -ComputerName 'localhost' ^
                                -Port 8084 ^
                                -WarningAction SilentlyContinue; ^

                            if ($result.TcpTestSucceeded) { ^
                                Write-Host '================================'; ^
                                Write-Host 'TOMCAT IS UP ON PORT 8084'; ^
                                Write-Host '================================'; ^
                                $ready = $true; ^
                                break ^
                            ^

                            Write-Host 'Tomcat not ready... waiting 5 seconds'; ^
                            Start-Sleep -Seconds 5 ^
                        } ^

                        if (-not $ready) { ^
                            Write-Host 'Tomcat failed to start'; ^
                            exit 1 ^
                        }"
                    '''
                }
            }
        }


        // =====================================================
        // 13. VERIFY DEPLOYMENT
        // =====================================================

        stage('Verify Deployment') {

            steps {

                echo '============================================'
                echo 'VERIFYING DEPLOYMENT'
                echo '============================================'

                bat '''
                    echo.
                    echo ===== BACKEND =====

                    powershell -NoProfile -Command ^
                    "Test-NetConnection localhost -Port 8082"


                    echo.
                    echo ===== TOMCAT =====

                    powershell -NoProfile -Command ^
                    "Test-NetConnection localhost -Port 8084"


                    echo.
                    echo ===== APPZILLON WAR =====

                    if not exist "%TOMCAT_WEBAPPS%\\AppzillonServer.war" (
                        echo ERROR: AppzillonServer.war not deployed
                        exit /b 1
                    )

                    echo AppzillonServer.war deployed.


                    echo.
                    echo ===== QUIZAPP WAR =====

                    if not exist "%TOMCAT_WEBAPPS%\\quizapp.war" (
                        echo ERROR: quizapp.war not deployed
                        exit /b 1
                    )

                    echo quizapp.war deployed.


                    echo.
                    echo ============================================
                    echo BACKEND + APPZILLON DEPLOYMENT SUCCESSFUL
                    echo ============================================
                '''
            }
        }
    }


    // =========================================================
    // POST
    // =========================================================

    post {

        success {

            echo '''
====================================================
              DEPLOYMENT SUCCESSFUL
====================================================

Backend:
http://localhost:8082

Tomcat:
http://localhost:8084

Appzillon WAR:
quizapp.war

====================================================
'''
        }


        failure {

            echo '''
====================================================
              DEPLOYMENT FAILED
====================================================
'''

            bat '''
                echo.
                echo ============================================
                echo BACKEND LOG
                echo ============================================

                if exist "%BACKEND_LOG%" (
                    type "%BACKEND_LOG%"
                )


                echo.
                echo ============================================
                echo BACKEND ERROR LOG
                echo ============================================

                if exist "%BACKEND_ERROR_LOG%" (
                    type "%BACKEND_ERROR_LOG%"
                )


                echo.
                echo ============================================
                echo TOMCAT LOG DIRECTORY
                echo ============================================

                echo %TOMCAT_HOME%\\logs
            '''
        }
    }
}
