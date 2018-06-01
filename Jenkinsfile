pipeline {
  agent {
    node {
      label 'docker-slave'
    }

  }
  stages {
    stage('Build') {
      steps {
        sh 'echo "Hello"'
      }
      post {
        success {
          archiveArtifacts 'target/*.hpi,target/*.jpi'

        }

      }
    }
  }
  environment {
    TEST = 'yes'
  }
}