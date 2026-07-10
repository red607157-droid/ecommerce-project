pipeline {
    agent any

    environment {
        IMAGE_NAME = 'end17072007/ecommerce-project-backend'
        IMAGE_TAG  = "build-${BUILD_NUMBER}"
        RAILWAY_TOKEN = credentials('railway-token')
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Cloning source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building with Maven...'
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                bat 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                bat "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                bat "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing to Docker Hub...'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    bat "docker login -u %DOCKER_USERNAME% -p %DOCKER_PASSWORD%"
                    bat "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    bat "docker push ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy to Railway') {
            steps {
                echo 'Triggering deployment to Railway...'
                bat "npx @railway/cli up --service ecommerce-backend --detach"
            }
        }

        stage('Done') {
            steps {
                echo "Pipeline complete! Image: ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }
    }

    post {
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo 'Build failed. Check logs above.'
        }
    }
}