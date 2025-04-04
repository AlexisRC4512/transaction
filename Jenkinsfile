pipeline {
  agent any

  environment {
    IMAGE_NAME = "alexis4512/app"
    TAG = "v1.0.${BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') {
      steps {
        git 'https://github.com/tuusuario/tu-repo.git'
      }
    }

    stage('Build Docker Image') {
      steps {
        sh "docker build -t ${IMAGE_NAME}:${TAG} ."
      }
    }

    stage('Push Docker Image') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh """
            echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
            docker push ${IMAGE_NAME}:${TAG}
          """
        }
      }
    }

    stage('Deploy to Minikube') {
      steps {
        sh """
          sed 's|<IMAGE_TAG>|${TAG}|g' deployment.yaml | kubectl apply -f -
        """
      }
    }
  }
}