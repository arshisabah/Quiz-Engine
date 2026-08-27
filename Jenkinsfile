pipeline {

    agent any

    // =========================================================
    // PIPELINE OPTIONS
    // =========================================================

    options {
        skipDefaultCheckout(true)

        timeout(
            time: 30,
            unit: 'MINUTES'
        )
    }


    // =========================================================
    // CONFIGURATION
    //
    // CHANGE ONLY THIS SECTION FOR A DIFFERENT PROJECT
    // =========================================================

    environment {

        // =====================================================
        // GIT
        // =====================================================

        GIT_REPOSITORY =
            'https://github.com/arshisabah/Quiz-Engine.git'

        GIT_BRANCH =
            'main'


        // =====================================================
        // BACKEND
        // =====================================================

        BACKEND_PORT =
            '8082'

        BACKEND_JAR_NAME =
            'quizapp-0.0.1-SNAPSHOT.jar'

        BACKEND_DEPLOY_DIR =
            'D:\\Deployment\\backend'

        BACKEND_LOG =
            'D:\\Deployment\\backend\\backend.log'

        BACKEND_ERROR_LOG =
            'D:\\Deployment\\backend\\backend-error.log'

        BACKEND_PID_FILE =
            'D:\\Deployment\\backend\\backend.pid'


        // =====================================================
        // TOMCAT
        // =====================================================

        TOMCAT_HOME =
            'D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53'

        TOMCAT_PORT =
            '8084'

        TOMCAT_WEBAPPS =
            'D:\\Softwarespath\\apache-tomcat-9.0.53\\apache-tomcat-9.0.53\\webapps'


        // =====================================================
        // APPZILLON / FRONTEND
        // =====================================================

        APPZILLON_DEPLOY_DIR =
            'D:\\Deployment\\appzillon'

        APPZILLON_SERVER_WAR =
            'AppzillonServer.war'

        FRONTEND_WAR =
            'quizapp.war'


        // =====================================================
        // PLAYWRIGHT
        // =====================================================

        PLAYWRIGHT_DIR =
            'D:\\Deployment\\scripts'
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

                echo '''
============================================================
                    CHECKOUT SOURCE
============================================================
'''

                git(
                    branch: "${GIT_BRANCH}",
                    url: "${GIT_REPOSITORY}"
                )
            }
        }


        // =====================================================
        // 2. CHECK ENVIRONMENT
        // =====================================================

        stage('Check Environment') {

            steps {

                echo '''
============================================================
                  CHECK ENVIRONMENT
============================================================
'''

                bat '''
                    echo.
                    echo ================= JAVA =================
                    java -version

                    if errorlevel 1 (
                        echo ERROR: Java not available
                        exit /b 1
                    )


                    echo.
                    echo ================= MAVEN =================
                    mvn -version

                    if errorlevel 1 (
                        echo ERROR: Maven not available
                        exit /b 1
                    )


                    echo.
                    echo ================= NODE =================
                    node -v

                    if errorlevel 1 (
                        echo ERROR: Node.js not available
                        exit /b 1
                    )


                    echo.
                    echo ================= NPM ==================
                    npm -v

                    if errorlevel 1 (
                        echo ERROR: npm not available
                        exit /b 1
                    )


                    echo.
                    echo ================= TOMCAT ===============

                    if not exist "%TOMCAT_HOME%" (
                        echo ERROR: Tomcat directory not found:
                        echo %TOMCAT_HOME%
                        exit /b 1
                    )

                    echo Tomcat found:
                    echo %TOMCAT_HOME%


                    echo.
                    echo ============= BACKEND DIRECTORY =========

                    if not exist "%BACKEND_DEPLOY_DIR%" (
                        mkdir "%BACKEND_DEPLOY_DIR%"
                    )

                    echo Backend deployment directory:
                    echo %BACKEND_DEPLOY_DIR%


                    echo.
                    echo ============ APPZILLON DIRECTORY =========

                    if not exist "%APPZILLON_DEPLOY_DIR%" (
                        echo ERROR: Appzillon directory not found:
                        echo %APPZILLON_DEPLOY_DIR%
                        exit /b 1
                    )


                    echo.
                    echo ============= APPZILLON SERVER WAR =========

                    if not exist "%APPZILLON_DEPLOY_DIR%\\%APPZILLON_SERVER_WAR%" (
                        echo ERROR: AppzillonServer WAR not found
                        echo %APPZILLON_DEPLOY_DIR%\\%APPZILLON_SERVER_WAR%
                        exit /b 1
                    )


                    echo.
                    echo ================ FRONTEND WAR ===============

                    if not exist "%APPZILLON_DEPLOY_DIR%\\%FRONTEND_WAR%" (
                        echo ERROR: Frontend WAR not found
                        echo %APPZILLON_DEPLOY_DIR%\\%FRONTEND_WAR%
                        exit /b 1
                    )


                    echo.
                    echo ================ PLAYWRIGHT =================

                    if not exist "%PLAYWRIGHT_DIR%" (
                        echo ERROR: Playwright directory not found
                        echo %PLAYWRIGHT_DIR%
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
                        echo ERROR: Playwright tests directory not found
                        exit /b 1
                    )


                    echo.
                    echo ============================================
                    echo ENVIRONMENT CHECK PASSED
                    echo ============================================
                '''
            }
        }


        // =====================================================
        // 3. BUILD BACKEND
        // =====================================================

        stage('Build Backend') {

            steps {

                echo '''
============================================================
                  BUILD BACKEND
============================================================
'''

                bat '''
                    mvn clean package -DskipTests

                    if errorlevel 1 (
                        echo ERROR: Maven build failed
                        exit /b 1
                    )


                    if not exist "target\\%BACKEND_JAR_NAME%" (
                        echo ERROR: Backend JAR was not generated
                        echo target\\%BACKEND_JAR_NAME%
                        exit /b 1
                    )


                    echo.
                    echo ============================================
                    echo BACKEND BUILD SUCCESSFUL
                    echo ============================================
                '''
            }
        }


        // =====================================================
        // 4. DEPLOY BACKEND
        // =====================================================

        stage('Deploy Backend') {

            steps {

                echo '''
============================================================
                  DEPLOY BACKEND
============================================================
'''


                // -------------------------------------------------
                // STOP OLD BACKEND
                // -------------------------------------------------

                bat '''

                    echo Stopping existing backend...


                    powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like '*%BACKEND_JAR_NAME%*' } | ForEach-Object { Write-Host ('Stopping backend PID: ' + $_.ProcessId); Stop-Process -Id $_.ProcessId -Force }"


                    powershell.exe -NoProfile -Command "Start-Sleep -Seconds 3"
                '''


                // -------------------------------------------------
                // COPY NEW JAR
                // -------------------------------------------------

                bat '''

                    echo Copying backend JAR...


                    copy /Y ^
                    "target\\%BACKEND_JAR_NAME%" ^
                    "%BACKEND_DEPLOY_DIR%\\%BACKEND_JAR_NAME%"


                    if errorlevel 1 (
                        echo ERROR: Backend JAR copy failed
                        exit /b 1
                    )


                    if not exist "%BACKEND_DEPLOY_DIR%\\%BACKEND_JAR_NAME%" (
                        echo ERROR: Backend JAR not found after copy
                        exit /b 1
                    )
                '''


                // -------------------------------------------------
                // START BACKEND
                // -------------------------------------------------

                bat '''

                    echo Starting backend...


                    if exist "%BACKEND_LOG%" (
                        del /F /Q "%BACKEND_LOG%"
                    )


                    if exist "%BACKEND_ERROR_LOG%" (
                        del /F /Q "%BACKEND_ERROR_LOG%"
                    )


                    if exist "%BACKEND_PID_FILE%" (
                        del /F /Q "%BACKEND_PID_FILE%"
                    )


                    set JENKINS_NODE_COOKIE=dontKillMe


                    powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "$jar = '%BACKEND_DEPLOY_DIR%\\%BACKEND_JAR_NAME%'; $p = Start-Process -FilePath 'java' -ArgumentList '-jar',$jar -WorkingDirectory '%BACKEND_DEPLOY_DIR%' -RedirectStandardOutput '%BACKEND_LOG%' -RedirectStandardError '%BACKEND_ERROR_LOG%' -PassThru; Set-Content '%BACKEND_PID_FILE%' $p.Id; Write-Host ('Backend started with PID: ' + $p.Id)"
                '''
            }
        }


        // =====================================================
        // 5. WAIT FOR BACKEND
        // =====================================================

        stage('Wait For Backend') {

            steps {

                echo '''
============================================================
                  WAIT FOR BACKEND
============================================================
'''

                timeout(
                    time: 90,
                    unit: 'SECONDS'
                ) {

                    powershell '''

                        $ready = $false


                        for ($i = 0; $i -lt 18; $i++) {

                            Write-Host "Checking backend on port $env:BACKEND_PORT..."


                            $result = Test-NetConnection `
                                -ComputerName "localhost" `
                                -Port $env:BACKEND_PORT `
                                -WarningAction SilentlyContinue


                            if ($result.TcpTestSucceeded) {

                                Write-Host ""
                                Write-Host "============================================"
                                Write-Host "BACKEND IS READY"
                                Write-Host "PORT: $env:BACKEND_PORT"
                                Write-Host "============================================"

                                $ready = $true

                                break
                            }


                            Write-Host "Backend not ready."

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
        // 6. STOP TOMCAT
        // =====================================================

        stage('Stop Tomcat') {

            steps {

                echo '''
============================================================
                    STOP TOMCAT
============================================================
'''

                bat '''

                    call "%TOMCAT_HOME%\\bin\\shutdown.bat"


                    powershell.exe -NoProfile -Command "Start-Sleep -Seconds 10"


                    echo Tomcat stop command completed.
                '''
            }
        }


        // =====================================================
        // 7. CLEAN TOMCAT
        // =====================================================

        stage('Clean Tomcat') {

            steps {

                echo '''
============================================================
                    CLEAN TOMCAT
============================================================
'''

                bat '''

                    echo Removing old AppzillonServer WAR...


                    if exist "%TOMCAT_WEBAPPS%\\%APPZILLON_SERVER_WAR%" (
                        del /F /Q "%TOMCAT_WEBAPPS%\\%APPZILLON_SERVER_WAR%"
                    )


                    echo Removing old frontend WAR...


                    if exist "%TOMCAT_WEBAPPS%\\%FRONTEND_WAR%" (
                        del /F /Q "%TOMCAT_WEBAPPS%\\%FRONTEND_WAR%"
                    )


                    echo Removing old extracted AppzillonServer directory...


                    if exist "%TOMCAT_WEBAPPS%\\AppzillonServer" (
                        rmdir /S /Q "%TOMCAT_WEBAPPS%\\AppzillonServer"
                    )


                    echo Removing old extracted frontend directory...


                    if exist "%TOMCAT_WEBAPPS%\\%FRONTEND_WAR:.war=%" (
                        rmdir /S /Q "%TOMCAT_WEBAPPS%\\%FRONTEND_WAR:.war=%"
                    )


                    echo Tomcat cleaned.
                '''
            }
        }


        // =====================================================
        // 8. DEPLOY APPZILLON / FRONTEND
        // =====================================================

        stage('Deploy Frontend') {

            steps {

                echo '''
============================================================
              DEPLOY APPZILLON / FRONTEND
============================================================
'''

                bat '''

                    echo Deploying AppzillonServer...


                    copy /Y ^
                    "%APPZILLON_DEPLOY_DIR%\\%APPZILLON_SERVER_WAR%" ^
                    "%TOMCAT_WEBAPPS%\\%APPZILLON_SERVER_WAR%"


                    if errorlevel 1 (
                        echo ERROR: AppzillonServer deployment failed
                        exit /b 1
                    )


                    echo.
                    echo Deploying frontend...


                    copy /Y ^
                    "%APPZILLON_DEPLOY_DIR%\\%FRONTEND_WAR%" ^
                    "%TOMCAT_WEBAPPS%\\%FRONTEND_WAR%"


                    if errorlevel 1 (
                        echo ERROR: Frontend deployment failed
                        exit /b 1
                    )


                    echo.
                    echo ============================================
                    echo FRONTEND DEPLOYMENT SUCCESSFUL
                    echo ============================================
                '''
            }
        }


        // =====================================================
        // 9. START TOMCAT
        // =====================================================

        stage('Start Tomcat') {

            steps {

                echo '''
============================================================
                    START TOMCAT
============================================================
'''

                bat '''

                    set JENKINS_NODE_COOKIE=dontKillMe


                    call "%TOMCAT_HOME%\\bin\\startup.bat"


                    powershell.exe -NoProfile -Command "Start-Sleep -Seconds 10"


                    echo Tomcat start command completed.
                '''
            }
        }


        // =====================================================
        // 10. WAIT FOR TOMCAT
        // =====================================================

        stage('Wait For Tomcat') {

            steps {

                echo '''
============================================================
                  WAIT FOR TOMCAT
============================================================
'''

                timeout(
                    time: 90,
                    unit: 'SECONDS'
                ) {

                    powershell '''

                        $ready = $false


                        for ($i = 0; $i -lt 18; $i++) {

                            Write-Host "Checking Tomcat on port $env:TOMCAT_PORT..."


                            $result = Test-NetConnection `
                                -ComputerName "localhost" `
                                -Port $env:TOMCAT_PORT `
                                -WarningAction SilentlyContinue


                            if ($result.TcpTestSucceeded) {

                                Write-Host ""
                                Write-Host "============================================"
                                Write-Host "TOMCAT IS READY"
                                Write-Host "PORT: $env:TOMCAT_PORT"
                                Write-Host "============================================"

                                $ready = $true

                                break
                            }


                            Write-Host "Tomcat not ready."

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
        // 11. WAIT FOR FRONTEND
        // =====================================================

        stage('Wait For Frontend') {

            steps {

                echo '''
============================================================
              WAIT FOR FRONTEND
============================================================
'''

                powershell '''

                    Write-Host "Tomcat is running."

                    Write-Host "Waiting for web applications to initialize..."

                    Start-Sleep -Seconds 20

                    Write-Host "Frontend initialization completed."
                '''
            }
        }


        // =====================================================
        // 12. VERIFY SERVICES
        // =====================================================

        stage('Verify Services') {

            steps {

                echo '''
============================================================
                 VERIFY SERVICES
============================================================
'''

                powershell '''

                    Write-Host ""
                    Write-Host "Checking backend..."


                    $backend = Test-NetConnection `
                        -ComputerName "localhost" `
                        -Port $env:BACKEND_PORT `
                        -WarningAction SilentlyContinue


                    if (-not $backend.TcpTestSucceeded) {

                        Write-Error "Backend is not running."

                        exit 1
                    }


                    Write-Host "Backend :$env:BACKEND_PORT -> OK"


                    Write-Host ""
                    Write-Host "Checking Tomcat..."


                    $tomcat = Test-NetConnection `
                        -ComputerName "localhost" `
                        -Port $env:TOMCAT_PORT `
                        -WarningAction SilentlyContinue


                    if (-not $tomcat.TcpTestSucceeded) {

                        Write-Error "Tomcat is not running."

                        exit 1
                    }


                    Write-Host "Tomcat :$env:TOMCAT_PORT -> OK"


                    Write-Host ""
                    Write-Host "Checking AppzillonServer WAR..."


                    if (-not (Test-Path "$env:TOMCAT_WEBAPPS\\$env:APPZILLON_SERVER_WAR")) {

                        Write-Error "AppzillonServer WAR not found."

                        exit 1
                    }


                    Write-Host "AppzillonServer WAR -> OK"


                    Write-Host ""
                    Write-Host "Checking frontend WAR..."


                    if (-not (Test-Path "$env:TOMCAT_WEBAPPS\\$env:FRONTEND_WAR")) {

                        Write-Error "Frontend WAR not found."

                        exit 1
                    }


                    Write-Host "Frontend WAR -> OK"


                    Write-Host ""
                    Write-Host "============================================"
                    Write-Host "ALL SERVICES VERIFIED"
                    Write-Host "============================================"
                '''
            }
        }


        // =====================================================
        // 13. RUN PLAYWRIGHT
        // =====================================================

        stage('Run Playwright Tests') {

            steps {

                echo '''
============================================================
                RUN PLAYWRIGHT TESTS
============================================================
'''


                // -------------------------------------------------
                // PLAYWRIGHT DIRECTORY
                // -------------------------------------------------

                bat '''
                    cd /D "%PLAYWRIGHT_DIR%"

                    echo.
                    echo ============================================
                    echo PLAYWRIGHT DIRECTORY
                    echo ============================================

                    cd
                '''


                // -------------------------------------------------
                // NODE
                // -------------------------------------------------

                bat '''
                    cd /D "%PLAYWRIGHT_DIR%"

                    echo.
                    echo ============================================
                    echo NODE VERSION
                    echo ============================================

                    node -v
                '''


                // -------------------------------------------------
                // NPM
                // -------------------------------------------------

                bat '''
                    cd /D "%PLAYWRIGHT_DIR%"

                    echo.
                    echo ============================================
                    echo NPM VERSION
                    echo ============================================

                    npm -v
                '''


                // -------------------------------------------------
                // PLAYWRIGHT VERSION
                // -------------------------------------------------

                bat '''
                    cd /D "%PLAYWRIGHT_DIR%"

                    echo.
                    echo ============================================
                    echo PLAYWRIGHT VERSION
                    echo ============================================

                    npx playwright --version

                    if errorlevel 1 (
                        echo ERROR: Playwright not available
                        exit /b 1
                    )
                '''


                // -------------------------------------------------
                // INSTALL NODE DEPENDENCIES
                // -------------------------------------------------

                bat '''
                    cd /D "%PLAYWRIGHT_DIR%"

                    echo.
                    echo ============================================
                    echo INSTALLING NODE DEPENDENCIES
                    echo ============================================

                    npm ci

                    if errorlevel 1 (
                        echo ERROR: npm ci failed
                        exit /b 1
                    )
                '''


                // -------------------------------------------------
                // INSTALL CHROMIUM
                // -------------------------------------------------

                bat '''
                    cd /D "%PLAYWRIGHT_DIR%"

                    echo.
                    echo ============================================
                    echo INSTALLING CHROMIUM
                    echo ============================================

                    npx playwright install chromium

                    if errorlevel 1 (
                        echo ERROR: Chromium installation failed
                        exit /b 1
                    )
                '''


                // -------------------------------------------------
                // CLEAN OLD RESULTS
                // -------------------------------------------------

                bat '''
                    cd /D "%PLAYWRIGHT_DIR%"

                    echo.
                    echo ============================================
                    echo CLEANING OLD PLAYWRIGHT RESULTS
                    echo ============================================

                    if exist "playwright-report" (
                        rmdir /S /Q "playwright-report"
                    )


                    if exist "test-results" (
                        rmdir /S /Q "test-results"
                    )
                '''


                // -------------------------------------------------
                // RUN TESTS
                // -------------------------------------------------

                bat '''
                    cd /D "%PLAYWRIGHT_DIR%"

                    echo.
                    echo ============================================
                    echo STARTING PLAYWRIGHT TESTS
                    echo ============================================

                    npx playwright test

                    if errorlevel 1 (
                        echo.
                        echo ============================================
                        echo PLAYWRIGHT TESTS FAILED
                        echo ============================================
                        exit /b 1
                    )


                    echo.
                    echo ============================================
                    echo PLAYWRIGHT TESTS PASSED
                    echo ============================================
                '''
            }
        }
    }


    // =========================================================
    // POST BUILD
    // =========================================================

    post {

        // =====================================================
        // ALWAYS
        // =====================================================

        always {

            echo '''
============================================================
              COLLECTING TEST RESULTS
============================================================
'''


            // -------------------------------------------------
            // COPY PLAYWRIGHT HTML REPORT
            // -------------------------------------------------

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

                    echo Playwright HTML report not found.
                )
            '''


            // -------------------------------------------------
            // COPY TEST ARTIFACTS
            // -------------------------------------------------

            bat '''

                if exist "test-results" (
                    rmdir /S /Q "test-results"
                )


                if exist "%PLAYWRIGHT_DIR%\\test-results" (

                    xcopy /E /I /Y ^
                    "%PLAYWRIGHT_DIR%\\test-results" ^
                    "test-results"

                    echo Playwright test artifacts copied.

                ) else (

                    echo Playwright test-results not found.
                )
            '''


            // -------------------------------------------------
            // ARCHIVE ARTIFACTS
            // -------------------------------------------------

            archiveArtifacts(
                artifacts: 'test-results/**/*',
                allowEmptyArchive: true,
                fingerprint: true
            )
        }


        // =====================================================
        // SUCCESS
        // =====================================================

        success {

            echo '''
============================================================
                    PIPELINE SUCCESS
============================================================

                    BUILD SUCCESSFUL

------------------------------------------------------------

Backend:
RUNNING

Backend Port:
${BACKEND_PORT}

------------------------------------------------------------

Tomcat:
RUNNING

Tomcat Port:
${TOMCAT_PORT}

------------------------------------------------------------

Frontend:
DEPLOYED

------------------------------------------------------------

Playwright:
ALL TESTS PASSED

============================================================
'''
        }


        // =====================================================
        // FAILURE
        // =====================================================

        failure {

            echo '''
============================================================
                    PIPELINE FAILED
============================================================

Check the Jenkins console output.

Possible failure areas:

1. Git checkout
2. Environment
3. Maven build
4. Backend startup
5. Backend port
6. Tomcat startup
7. WAR deployment
8. Frontend initialization
9. Playwright
10. Browser installation

Playwright artifacts:

test-results/

============================================================
'''
        }
    }
}
