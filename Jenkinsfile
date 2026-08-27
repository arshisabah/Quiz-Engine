pipeline {

    agent any

    // =========================================================
    // IMPORTANT:
    // Jenkins automatically checks out the repository.
    // We are doing our own checkout below.
    // =========================================================

    options {
        skipDefaultCheckout(true)

        // Prevent Jenkins from waiting forever
        timeout(time: 15, unit: 'MINUTES')
    }


    // =========================================================
    // ENVIRONMENT
    // =========================================================

    environment {

        // -----------------------------------------------------
        // LOCAL DEPLOYMENT
        // -----------------------------------------------------

        DEPLOYMENT_DIR = 'D:\\Deployment'

        BACKEND_DIR = 'D:\\Deployment\\backend'

        APPZILLON_DIR = 'D:\\Deployment\\appzillon'


        // -----------------------------------------------------
        // APPZILLON WAR FILES
        // -----------------------------------------------------

        APPZILLON_SERVER_WAR =
            'D:\\Deployment\\appzillon\\AppzillonServer.war'

        QUIZAPP_WAR =
            'D:\\Deployment\\appzillon\\quizapp.war'


        // -----------------------------------------------------
        // BACKEND
        // -----------------------------------------------------

        BACKEND_JAR =
            'D:\\Deployment\\backend\\quizapp-0.0.1-SNAPSHOT.jar'

        BACKEND_LOG =
            'D:\\Deployment\\backend\\backend.log'

        BACKEND_ERROR_LOG =
            'D:\\Deployment\\backend\\backend-error.log'

        BACKEND_PID =
            'D:\\Deployment\\backend\\backend.pid'


        // -----------------------------------------------------
        // TOMCAT
        // -----------------------------------------------------

        TOMCAT_HOME =
            'D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53'

        TOMCAT_WEBAPPS =
            'D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53\\webapps'


        // -----------------------------------------------------
        // PORTS
        // -----------------------------------------------------

        BACKEND_PORT = '8082'

        TOMCAT_PORT = '8084'
    }


    // =========================================================
    // STAGES
    // =========================================================

    stages {


        // =====================================================
        // 1. CHECKOUT
        // =====================================================

        stage('Checkout') {

            steps {

                echo '============================================'
                echo 'CHECKOUT QUIZ ENGINE BACKEND'
                echo '============================================'

                git(
                    branch: 'main',
                    url: 'https://github.com/arshisabah/Quiz-Engine.git'
                )
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
                    echo ==================== JAVA ====================

                    java -version


                    echo.
                    echo ==================== MAVEN ====================

                    mvn -version


                    echo.
                    echo ==================== TOMCAT ====================

                    if not exist "%TOMCAT_HOME%" (
                        echo ERROR: Tomcat directory not found
                        echo %TOMCAT_HOME%
                        exit /b 1
                    )

                    echo Tomcat found:
                    echo %TOMCAT_HOME%


                    echo.
                    echo ==================== APPZILLON SERVER ====================

                    if not exist "%APPZILLON_SERVER_WAR%" (
                        echo ERROR: AppzillonServer.war not found
                        echo %APPZILLON_SERVER_WAR%
                        exit /b 1
                    )

                    echo AppzillonServer.war found


                    echo.
                    echo ==================== QUIZAPP ====================

                    if not exist "%QUIZAPP_WAR%" (
                        echo ERROR: quizapp.war not found
                        echo %QUIZAPP_WAR%
                        exit /b 1
                    )

                    echo quizapp.war found


                    echo.
                    echo ==================== DEPLOYMENT ====================

                    if not exist "%BACKEND_DIR%" (
                        mkdir "%BACKEND_DIR%"
                    )

                    echo Environment check completed successfully.
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
                    if not exist "target\\quizapp-0.0.1-SNAPSHOT.jar" (
                        echo ERROR: Backend JAR was not generated.
                        exit /b 1
                    )


                    copy /Y ^
                    "target\\quizapp-0.0.1-SNAPSHOT.jar" ^
                    "%BACKEND_JAR%"


                    if not exist "%BACKEND_JAR%" (
                        echo ERROR: Backend JAR copy failed.
                        exit /b 1
                    )


                    echo.
                    echo Backend JAR copied successfully:
                    echo %BACKEND_JAR%
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

                    powershell.exe -NoProfile -Command "Start-Sleep -Seconds 3"

                    echo Old backend stopped.
                '''
            }
        }


        // =====================================================
        // 6. START BACKEND
        // =====================================================

        stage('Start Backend') {

            steps {

                echo '============================================'
                echo 'STARTING SPRING BOOT BACKEND'
                echo 'PORT: 8082'
                echo '============================================'

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


                    set JENKINS_NODE_COOKIE=dontKillMe


                    powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "$p = Start-Process -FilePath 'java' -ArgumentList '-jar','%BACKEND_JAR%' -WorkingDirectory '%BACKEND_DIR%' -RedirectStandardOutput '%BACKEND_LOG%' -RedirectStandardError '%BACKEND_ERROR_LOG%' -PassThru; Set-Content '%BACKEND_PID%' $p.Id; Write-Host ('Backend started with PID: ' + $p.Id)"


                    echo.
                    echo Backend start command completed.
                '''
            }
        }


        // =====================================================
        // 7. WAIT FOR BACKEND
        // =====================================================

        stage('Wait For Backend') {

            steps {

                echo '============================================'
                echo 'WAITING FOR BACKEND'
                echo 'PORT: 8082'
                echo '============================================'

                timeout(time: 90, unit: 'SECONDS') {

                    powershell '''
                        $ready = $false

                        for ($i = 0; $i -lt 18; $i++) {

                            Write-Host "Checking backend on port 8082..."

                            $result = Test-NetConnection `
                                -ComputerName "localhost" `
                                -Port 8082 `
                                -WarningAction SilentlyContinue

                            if ($result.TcpTestSucceeded) {

                                Write-Host ""
                                Write-Host "============================================"
                                Write-Host "BACKEND IS UP"
                                Write-Host "PORT: 8082"
                                Write-Host "============================================"

                                $ready = $true
                                break
                            }

                            Write-Host "Backend not ready."
                            Write-Host "Waiting 5 seconds..."

                            Start-Sleep -Seconds 5
                        }

                        if (-not $ready) {

                            Write-Host ""
                            Write-Host "Backend failed to start."

                            exit 1
                        }
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

                    powershell.exe -NoProfile -Command "Start-Sleep -Seconds 10"

                    echo Tomcat stop command completed.
                '''
            }
        }


        // =====================================================
        // 9. CLEAN OLD TOMCAT DEPLOYMENT
        // =====================================================

        stage('Clean Tomcat') {

            steps {

                echo '============================================'
                echo 'CLEANING OLD TOMCAT DEPLOYMENT'
                echo '============================================'

                bat '''
                    echo.
                    echo Removing old AppzillonServer.war...

                    if exist "%TOMCAT_WEBAPPS%\\AppzillonServer.war" (
                        del /F /Q "%TOMCAT_WEBAPPS%\\AppzillonServer.war"
                    )


                    echo.
                    echo Removing old quizapp.war...

                    if exist "%TOMCAT_WEBAPPS%\\quizapp.war" (
                        del /F /Q "%TOMCAT_WEBAPPS%\\quizapp.war"
                    )


                    echo.
                    echo Removing old AppzillonServer directory...

                    if exist "%TOMCAT_WEBAPPS%\\AppzillonServer" (
                        rmdir /S /Q "%TOMCAT_WEBAPPS%\\AppzillonServer"
                    )


                    echo.
                    echo Removing old quizapp directory...

                    if exist "%TOMCAT_WEBAPPS%\\quizapp" (
                        rmdir /S /Q "%TOMCAT_WEBAPPS%\\quizapp"
                    )


                    echo.
                    echo Old deployment cleaned.
                '''
            }
        }


        // =====================================================
        // 10. DEPLOY APPZILLON WAR FILES
        // =====================================================

        stage('Deploy Appzillon') {

            steps {

                echo '============================================'
                echo 'DEPLOYING APPZILLON WAR FILES'
                echo '============================================'

                bat '''
                    echo.
                    echo Copying AppzillonServer.war...

                    copy /Y ^
                    "%APPZILLON_SERVER_WAR%" ^
                    "%TOMCAT_WEBAPPS%\\AppzillonServer.war"


                    if errorlevel 1 (
                        echo ERROR: Failed to copy AppzillonServer.war
                        exit /b 1
                    )


                    echo.
                    echo Copying quizapp.war...

                    copy /Y ^
                    "%QUIZAPP_WAR%" ^
                    "%TOMCAT_WEBAPPS%\\quizapp.war"


                    if errorlevel 1 (
                        echo ERROR: Failed to copy quizapp.war
                        exit /b 1
                    )


                    echo.
                    echo ============================================
                    echo APPZILLON WAR FILES DEPLOYED
                    echo ============================================
                '''
            }
        }


        // =====================================================
        // 11. START TOMCAT
        // =====================================================

        stage('Start Tomcat') {

            steps {

                echo '============================================'
                echo 'STARTING TOMCAT'
                echo 'PORT: 8084'
                echo '============================================'

                bat '''
                    set JENKINS_NODE_COOKIE=dontKillMe

                    call "%TOMCAT_HOME%\\bin\\startup.bat"

                    powershell.exe -NoProfile -Command "Start-Sleep -Seconds 20"

                    echo.
                    echo Tomcat start command completed.
                '''
            }
        }


        // =====================================================
        // 12. WAIT FOR TOMCAT
        // =====================================================

        stage('Wait For Tomcat') {

            steps {

                echo '============================================'
                echo 'WAITING FOR TOMCAT'
                echo 'PORT: 8084'
                echo '============================================'

                timeout(time: 90, unit: 'SECONDS') {

                    powershell '''
                        $ready = $false

                        for ($i = 0; $i -lt 18; $i++) {

                            Write-Host "Checking Tomcat on port 8084..."

                            $result = Test-NetConnection `
                                -ComputerName "localhost" `
                                -Port 8084 `
                                -WarningAction SilentlyContinue

                            if ($result.TcpTestSucceeded) {

                                Write-Host ""
                                Write-Host "============================================"
                                Write-Host "TOMCAT IS UP"
                                Write-Host "PORT: 8084"
                                Write-Host "============================================"

                                $ready = $true
                                break
                            }

                            Write-Host "Tomcat not ready."
                            Write-Host "Waiting 5 seconds..."

                            Start-Sleep -Seconds 5
                        }

                        if (-not $ready) {

                            Write-Host ""
                            Write-Host "Tomcat failed to start."

                            exit 1
                        }
                    '''
                }
            }
        }


        // =====================================================
        // 13. WAIT FOR APPZILLON DEPLOYMENT
        // =====================================================

        stage('Wait For Appzillon Deployment') {

            steps {

                echo '============================================'
                echo 'WAITING FOR APPZILLON DEPLOYMENT'
                echo '============================================'

                timeout(time: 90, unit: 'SECONDS') {

                    powershell '''
                        $quizappReady = $false
                        $serverReady = $false

                        for ($i = 0; $i -lt 18; $i++) {

                            if (
                                (Test-Path "$env:TOMCAT_WEBAPPS\\quizapp") -and
                                (Test-Path "$env:TOMCAT_WEBAPPS\\AppzillonServer")
                            ) {

                                Write-Host ""
                                Write-Host "============================================"
                                Write-Host "APPZILLON APPLICATIONS DEPLOYED"
                                Write-Host "============================================"

                                $quizappReady = $true
                                $serverReady = $true

                                break
                            }

                            Write-Host "Waiting for Tomcat to unpack WAR files..."
                            Start-Sleep -Seconds 5
                        }

                        if (-not $quizappReady -or -not $serverReady) {

                            Write-Host ""
                            Write-Host "ERROR: Appzillon WAR files were not unpacked."

                            exit 1
                        }
                    '''
                }
            }
        }


        // =====================================================
        // 14. VERIFY DEPLOYMENT
        // =====================================================

        stage('Verify Deployment') {

            steps {

                echo '============================================'
                echo 'FINAL DEPLOYMENT VERIFICATION'
                echo '============================================'

                powershell '''
                    Write-Host ""
                    Write-Host "============================================"
                    Write-Host "CHECKING BACKEND :8082"
                    Write-Host "============================================"

                    $backend = Test-NetConnection `
                        -ComputerName "localhost" `
                        -Port 8082 `
                        -WarningAction SilentlyContinue

                    if (-not $backend.TcpTestSucceeded) {

                        Write-Error "Backend is NOT running on port 8082"
                        exit 1
                    }

                    Write-Host "Backend :8082 -> OK"


                    Write-Host ""
                    Write-Host "============================================"
                    Write-Host "CHECKING TOMCAT :8084"
                    Write-Host "============================================"

                    $tomcat = Test-NetConnection `
                        -ComputerName "localhost" `
                        -Port 8084 `
                        -WarningAction SilentlyContinue

                    if (-not $tomcat.TcpTestSucceeded) {

                        Write-Error "Tomcat is NOT running on port 8084"
                        exit 1
                    }

                    Write-Host "Tomcat :8084 -> OK"


                    Write-Host ""
                    Write-Host "============================================"
                    Write-Host "CHECKING APPZILLON SERVER WAR"
                    Write-Host "============================================"

                    if (-not (Test-Path "$env:TOMCAT_WEBAPPS\\AppzillonServer.war")) {

                        Write-Error "AppzillonServer.war not found"
                        exit 1
                    }

                    Write-Host "AppzillonServer.war -> OK"


                    Write-Host ""
                    Write-Host "============================================"
                    Write-Host "CHECKING QUIZAPP WAR"
                    Write-Host "============================================"

                    if (-not (Test-Path "$env:TOMCAT_WEBAPPS\\quizapp.war")) {

                        Write-Error "quizapp.war not found"
                        exit 1
                    }

                    Write-Host "quizapp.war -> OK"


                    Write-Host ""
                    Write-Host "============================================"
                    Write-Host "CHECKING DEPLOYED APPLICATION DIRECTORIES"
                    Write-Host "============================================"

                    if (-not (Test-Path "$env:TOMCAT_WEBAPPS\\AppzillonServer")) {

                        Write-Error "AppzillonServer application directory not found"
                        exit 1
                    }

                    Write-Host "AppzillonServer directory -> OK"


                    if (-not (Test-Path "$env:TOMCAT_WEBAPPS\\quizapp")) {

                        Write-Error "quizapp application directory not found"
                        exit 1
                    }

                    Write-Host "quizapp directory -> OK"


                    Write-Host ""
                    Write-Host "============================================"
                    Write-Host "DEPLOYMENT SUCCESSFUL"
                    Write-Host "============================================"

                    Write-Host ""
                    Write-Host "Backend : http://localhost:8082"
                    Write-Host "Tomcat  : http://localhost:8084"
                    Write-Host "Appzillon WAR : quizapp.war"
                '''
            }
        }


        // =====================================================
        // 15. FINAL SERVICE CHECK
        // =====================================================

        stage('Final Service Check') {

            steps {

                echo '============================================'
                echo 'FINAL SERVICE CHECK'
                echo '============================================'

                powershell '''
                    Write-Host ""
                    Write-Host "Checking backend..."

                    $backend = Test-NetConnection `
                        -ComputerName "localhost" `
                        -Port 8082 `
                        -WarningAction SilentlyContinue

                    if (-not $backend.TcpTestSucceeded) {

                        Write-Error "Backend stopped unexpectedly."
                        exit 1
                    }


                    Write-Host "Backend :8082 -> RUNNING"


                    Write-Host ""
                    Write-Host "Checking Tomcat..."

                    $tomcat = Test-NetConnection `
                        -ComputerName "localhost" `
                        -Port 8084 `
                        -WarningAction SilentlyContinue

                    if (-not $tomcat.TcpTestSucceeded) {

                        Write-Error "Tomcat stopped unexpectedly."
                        exit 1
                    }


                    Write-Host "Tomcat :8084 -> RUNNING"


                    Write-Host ""
                    Write-Host "============================================"
                    Write-Host "ALL SERVICES ARE RUNNING"
                    Write-Host "============================================"
                '''
            }
        }
    }


    // =========================================================
    // POST ACTIONS
    // =========================================================

    post {

        success {

            echo '''
============================================================
                 DEPLOYMENT SUCCESSFUL
============================================================

Backend:
http://localhost:8082

Tomcat:
http://localhost:8084

Appzillon:
http://localhost:8084/quizapp

Backend JAR:
D:\\Deployment\\backend\\quizapp-0.0.1-SNAPSHOT.jar

Tomcat:
D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53

============================================================
'''
        }


        failure {

            echo '''
============================================================
                 DEPLOYMENT FAILED
============================================================
'''

            bat '''
                echo.
                echo ==================================================
                echo BACKEND LOG
                echo ==================================================

                if exist "%BACKEND_LOG%" (
                    type "%BACKEND_LOG%"
                )


                echo.
                echo ==================================================
                echo BACKEND ERROR LOG
                echo ==================================================

                if exist "%BACKEND_ERROR_LOG%" (
                    type "%BACKEND_ERROR_LOG%"
                )


                echo.
                echo ==================================================
                echo TOMCAT LOG LOCATION
                echo ==================================================

                echo %TOMCAT_HOME%\\logs
            '''
        }
    }
}
