//Placeholder for programatically created name

jobName="First Maven Project"

pipelineJob(jobName)
{
  description('Compiles Maven Project')
  logRotator {
    daysToKeep(7)
  }
  parameters {
    stringParam('Job_Paramert',              'Hi This is Job parameter',                                   'This is test parameter for passing')
  definition {
    cps {
      script(readFileFromWorkspace('Pipelines/maven.groovy'))
    }
  }
}
}