pipeline {
  agent {
    node {
      label 'johan'
    }

  }
  stages {
    stage('Build') {
      steps {
        sh 'echo "Hello"'
      }
    }
  }
  environment {
    TEST = 'yes'
  }
}