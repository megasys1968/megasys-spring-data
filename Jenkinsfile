pipeline {
  agent any
  tools {
    maven 'maven3'
    jdk 'jdk11'
  }
  stages {
    stage ('Build') {
      steps {
        sh 'mvn clean package -Dmaven.test.skip=true'
      }
    }

    stage ('Test') {
      steps {
        sh 'mvn org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report -Dmaven.resources.skip=true -Dmaven.main.skip=true'
      }
    }

    stage ('Deploy') {
      steps {
        sh 'mvn deploy -Dmaven.resources.skip=true -Dmaven.main.skip=true -Dmaven.test.skip=true'
      }
    }
  }
}
