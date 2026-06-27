pipeline {
    agent any
    environment {
        IMAGE_NAME = 'ecommerce-project-backend'
        IMAGE_TAG = 'build-${BUILD_NUMBER}'
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
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Test') {
            steps {
                echo 'Running unit tests...'
                sh 'mvn test'
            }
        }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
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
            echo 'Build failed!'
        }
    }
}