- It's not clear what url the file command should print - the url of the image from the cat api, or the local file://
  url of where the image has been saved to. I'll be printing both.

- Requires maven to build.

- Execute build.sh to compile, run tests, and generate the executable jar in the target dir

- Execute run.sh to execute the jar, passing in the argument [ file | categories | fact ]

- Execute buildAndRun.sh to perform both the above