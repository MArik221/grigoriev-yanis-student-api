pipeline {
    agent any
    options {
        timestamps()
        disableConcurrentBuilds()
    }
    triggers {
        pollSCM('H/5 * * * *')
    }
    environment {
        IMAGE_NAME = 'grigoriev-yanis-student-api'
        IMAGE_TAG = 'v1.0-37038'
        STUDENT_NAME = 'Grigoriev Yanis'
        STUDENT_ID = '37038'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git log -1 --oneline'
            }
        }
        stage('Build') {
            steps {
                sh 'chmod +x gradlew scripts/fetch_gradle_wrapper.sh'
                sh './gradlew --no-daemon clean assemble'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew --no-daemon test'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -t "$IMAGE_NAME:$IMAGE_TAG" .'
            }
        }
        stage('Verify') {
            steps {
                sh 'docker image inspect "$IMAGE_NAME:$IMAGE_TAG" --format "{{.Id}} {{.Size}}"'
                echo "Student: ${env.STUDENT_NAME}; ID: ${env.STUDENT_ID}"
            }
        }
    }
    post {
        success { echo "SUCCESS: ${env.STUDENT_NAME} image ${env.IMAGE_NAME}:${env.IMAGE_TAG}" }
        failure { echo "FAILURE: ${env.STUDENT_NAME} - check failed stage in console" }
    }
}
