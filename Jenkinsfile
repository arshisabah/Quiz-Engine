pipeline {

    agent any


    // =========================================================
    // OPTIONS
    // =========================================================

    options {

        skipDefaultCheckout(true)

        timeout(time: 20, unit: 'MINUTES')
    }


    // =========================================================
    // ENVIRONMENT
    // =========================================================

    environment {

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


        // -----------------------------------------------------
        // PLAYWRIGHT
        // -----------------------------------------------------

        PLAYWRIGHT_DIR = 'D:\\Deployment\\scripts'
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
                    echo ==================== NODE ====================
                    node -v


                    echo.
                    echo ==================== NPM ====================
                    npm -v


                    echo.
                    echo ==================== TOMCAT ====================

                    if not exist "%TOMCAT_HOME%" (
                        echo ERROR: Tomcat directory not found
                        echo %TOMCAT_HOME%
                        exit /b 1
                    )

                    echo Tomcat found.


                    echo.
                    echo ==================== APPZILLON SERVER ====================

                    if not exist "%APPZILLON_SERVER_WAR%" (
                        echo ERROR: AppzillonServer.war not found
                        exit /b 1
                    )

                    echo AppzillonServer.war found.


                    echo.
                    echo ==================== QUIZAPP WAR ====================

                    if not exist "%QUIZAPP_WAR%" (
                        echo ERROR: quizapp.war not found
                        exit /b 1
                    )

                    echo quizapp.war found.


                    echo.
                    echo ==================== PLAYWRIGHT ====================

                    if not exist "%PLAYWRIGHT_DIR%" (
                        echo ERROR: Playwright directory not found
                        exit /b 1
                    )

                    if not exist "%PLAYWRIGHT_DIR%\\package.json" (
                        echo ERROR: package.json not found
                        exit /b 1
                    )

                    if not exist "%PLAYWRIGHT_DIR%\\package-lock.json" (
                        echo ERROR: package-lock.json not found
                        exit /b 1
                    )

                    if not exist "%PLAYWRIGHT_DIR%\\tests" (
                        echo ERROR: tests directory not found
                        exit /b 1
                    )

                    echo Playwright project found.


                    if not exist "%BACKEND_DIR%" (
                        mkdir "%BACKEND_DIR%"
                    )
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

                    powershell.exe -NoProfile -Command "Start-Sleep -Seconds 3"

                    echo Backend stopped.
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
                echo 'PORT: 8082'
                echo '============================================'

                bat '''
                    if exist "%BACKEND_LOG%" del /F /Q "%BACKEND_LOG%"

                    if exist "%BACKEND_ERROR_LOG%" del /F /Q "%BACKEND_ERROR_LOG%"

                    if exist "%BACKEND_PID%" del /F /Q "%BACKEND_PID%"


                    set JENKINS_NODE_COOKIE=dontKillMe


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

                    powershell '''

                        $ready = $false

                        for ($i = 0; $i -lt 18; $i++) {

                            Write-Host "Checking backend..."

                            $result = Test-NetConnection `
                                -ComputerName "localhost" `
                                -Port 8082 `
                                -WarningAction SilentlyContinue

                            if ($result.TcpTestSucceeded) {

                                Write-Host "BACKEND IS UP ON 8082"

                                $ready = $true
                                break
                            }

                            Start-Sleep -Seconds 5
                        }

                        if (-not $ready) {

                            Write-Error "Backend failed to start."

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
        // 9. CLEAN TOMCAT
        // =====================================================

        stage('Clean Tomcat') {

            steps {

                echo '============================================'
                echo 'CLEANING OLD APPZILLON DEPLOYMENT'
                echo '============================================'

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

                    echo Old Appzillon deployment cleaned.
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

                    copy /Y ^
                    "%APPZILLON_SERVER_WAR%" ^
                    "%TOMCAT_WEBAPPS%\\AppzillonServer.war"

                    if errorlevel 1 (
                        echo ERROR: AppzillonServer.war copy failed
                        exit /b 1
                    )


                    copy /Y ^
                    "%QUIZAPP_WAR%" ^
                    "%TOMCAT_WEBAPPS%\\quizapp.war"

                    if errorlevel 1 (
                        echo ERROR: quizapp.war copy failed
                        exit /b 1
                    )


                    echo.
                    echo Appzillon WAR files deployed.
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

                    set JENKINS_NODE_COOKIE=dontKillMe

                    call "%TOMCAT_HOME%\\bin\\startup.bat"

                    powershell.exe -NoProfile -Command "Start-Sleep -Seconds 20"

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
                echo 'WAITING FOR TOMCAT :8084'
                echo '============================================'

                timeout(time: 90, unit: 'SECONDS') {

                    powershell '''

                        $ready = $false

                        for ($i = 0; $i -lt 18; $i++) {

                            Write-Host "Checking Tomcat..."

                            $result = Test-NetConnection `
                                -ComputerName "localhost" `
                                -Port 8084 `
                                -WarningAction SilentlyContinue

                            if ($result.TcpTestSucceeded) {

                                Write-Host ""
                                Write-Host "============================================"
                                Write-Host "TOMCAT IS UP ON PORT 8084"
                                Write-Host "============================================"

                                $ready = $true
                                break
                            }

                            Start-Sleep -Seconds 5
                        }

                        if (-not $ready) {

                            Write-Error "Tomcat failed to start."

                            exit 1
                        }
                    '''
                }
            }
        }


        // =====================================================
        // 13. WAIT FOR APPZILLON
        // =====================================================

        stage('Wait For Appzillon') {

            steps {

                echo '============================================'
                echo 'WAITING FOR APPZILLON'
                echo '============================================'

                powershell '''

                    Write-Host "Tomcat is running."
                    Write-Host "Waiting for Appzillon applications to initialize..."

                    Start-Sleep -Seconds 20

                    Write-Host ""
                    Write-Host "Appzillon initialization wait completed."
                '''
            }
        }


        // =====================================================
        // 14. VERIFY DEPLOYMENT
        // =====================================================

        stage('Verify Deployment') {

            steps {

                echo '============================================'
                echo 'VERIFYING DEPLOYMENT'
                echo '============================================'

                powershell '''

                    Write-Host ""
                    Write-Host "Checking Backend :8082..."

                    $backend = Test-NetConnection `
                        -ComputerName "localhost" `
                        -Port 8082 `
                        -WarningAction SilentlyContinue

                    if (-not $backend.TcpTestSucceeded) {

                        Write-Error "Backend is not running."
                        exit 1
                    }

                    Write-Host "Backend :8082 -> OK"


                    Write-Host ""
                    Write-Host "Checking Tomcat :8084..."

                    $tomcat = Test-NetConnection `
                        -ComputerName "localhost" `
                        -Port 8084 `
                        -WarningAction SilentlyContinue

                    if (-not $tomcat.TcpTestSucceeded) {

                        Write-Error "Tomcat is not running."
                        exit 1
                    }

                    Write-Host "Tomcat :8084 -> OK"


                    Write-Host ""
                    Write-Host "Checking WAR files..."

                    if (-not (Test-Path "$env:TOMCAT_WEBAPPS\\AppzillonServer.war")) {

                        Write-Error "AppzillonServer.war missing."
                        exit 1
                    }

                    if (-not (Test-Path "$env:TOMCAT_WEBAPPS\\quizapp.war")) {

                        Write-Error "quizapp.war missing."
                        exit 1
                    }

                    Write-Host "AppzillonServer.war -> OK"
                    Write-Host "quizapp.war -> OK"


                    Write-Host ""
                    Write-Host "============================================"
                    Write-Host "DEPLOYMENT CHECK PASSED"
                    Write-Host "============================================"
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

                    $backend = Test-NetConnection `
                        -ComputerName "localhost" `
                        -Port 8082 `
                        -WarningAction SilentlyContinue

                    if (-not $backend.TcpTestSucceeded) {

                        Write-Error "Backend is down."
                        exit 1
                    }


                    $tomcat = Test-NetConnection `
                        -ComputerName "localhost" `
                        -Port 8084 `
                        -WarningAction SilentlyContinue

                    if (-not $tomcat.TcpTestSucceeded) {

                        Write-Error "Tomcat is down."
                        exit 1
                    }


                    Write-Host ""
                    Write-Host "============================================"
                    Write-Host "BACKEND + TOMCAT ARE RUNNING"
                    Write-Host "============================================"
                '''
            }
        }


        // =====================================================
        // 16. RUN PLAYWRIGHT
        // =====================================================

        stage('Run Playwright Tests') {
        
            steps {
        
                echo '============================================'
                echo 'RUNNING PLAYWRIGHT TESTS'
                echo '============================================'
        
                bat '''
                    cd /D "D:\\Deployment\\scripts"
        
                    echo.
                    echo ============================================
                    echo PLAYWRIGHT DIRECTORY
                    echo ============================================
        
                    cd
        
        
                    echo.
                    echo ============================================
                    echo NODE VERSION
                    echo ============================================
        
                    node -v
        
        
                    echo.
                    echo ============================================
                    echo NPM VERSION
                    echo ============================================
        
                    npm -v
        
        
                    echo.
                    echo ============================================
                    echo INSTALLING NODE DEPENDENCIES
                    echo ============================================
        
                    npm ci
        
                    if errorlevel 1 (
                        echo ERROR: npm ci failed
                        exit /b 1
                    )
        
        
                    echo.
                    echo ============================================
                    echo CHECKING PLAYWRIGHT
                    echo ============================================
        
                    npx playwright --version
        
                    if errorlevel 1 (
                        echo ERROR: Playwright is not available
                        exit /b 1
                    )
        
        
                    echo.
                    echo ============================================
                    echo INSTALLING CHROMIUM
                    echo ============================================
        
                    npx playwright install chromium
        
                    if errorlevel 1 (
                        echo ERROR: Chromium installation failed
                        exit /b 1
                    )
        
        
                    echo.
                    echo ============================================
                    echo CLEANING OLD RESULTS
                    echo ============================================
        
                    if exist "playwright-report" (
                        rmdir /S /Q "playwright-report"
                    )
        
                    if exist "test-results" (
                        rmdir /S /Q "test-results"
                    )
        
        
                    echo.
                    echo ============================================
                    echo STARTING PLAYWRIGHT TEST
                    echo ============================================
        
                    npx playwright test
        
                    if errorlevel 1 (
                        echo.
                        echo ============================================
                        echo PLAYWRIGHT TEST FAILED
                        echo ============================================
                        exit /b 1
                    )
        
        
                    echo.
                    echo ============================================
                    echo PLAYWRIGHT TEST PASSED
                    echo ============================================
                '''
            }
        }


    // =========================================================
    // POST
    // =========================================================

    post {


        // -----------------------------------------------------
        // ALWAYS
        // -----------------------------------------------------

        always {

            echo '============================================'
            echo 'COLLECTING PLAYWRIGHT RESULTS'
            echo '============================================'


            // Copy HTML report
            bat '''

                if exist "playwright-report" (
                    rmdir /S /Q "playwright-report"
                )


                if exist "%PLAYWRIGHT_DIR%\\playwright-report" (

                    xcopy /E /I /Y ^
                    "%PLAYWRIGHT_DIR%\\playwright-report" ^
                    "playwright-report"

                    echo Playwright report copied.

                ) else (

                    echo No Playwright HTML report found.
                )
            '''


            // Copy screenshots/videos/traces
            bat '''

                if exist "test-results" (
                    rmdir /S /Q "test-results"
                )


                if exist "%PLAYWRIGHT_DIR%\\test-results" (

                    xcopy /E /I /Y ^
                    "%PLAYWRIGHT_DIR%\\test-results" ^
                    "test-results"

                    echo Playwright test results copied.

                ) else (

                    echo No Playwright test-results found.
                )
            '''


            // Archive Playwright artifacts
            archiveArtifacts(
                artifacts: 'test-results/**/*',
                allowEmptyArchive: true,
                fingerprint: true
            )
        }


        // -----------------------------------------------------
        // SUCCESS
        // -----------------------------------------------------

        success {

            echo '''
============================================================
             DEPLOYMENT + PLAYWRIGHT PASSED
============================================================

Backend:
http://localhost:8082

Tomcat:
http://localhost:8084

Appzillon:
http://localhost:8084/quizapp

Playwright:
ALL TESTS PASSED

============================================================
'''
        }


        // -----------------------------------------------------
        // FAILURE
        // -----------------------------------------------------

        failure {

            echo '''
============================================================
             DEPLOYMENT OR TEST FAILED
============================================================

Check the Jenkins console output.

If Playwright failed, check:

test-results/

playwright-report/

============================================================
'''
        }
    }
}
