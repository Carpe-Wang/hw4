# Task 3 Submission

## GitHub Repository
[https://github.com/Carpe-Wang/task3](https://github.com/Carpe-Wang/task3)

## Docker Hub Image
[https://hub.docker.com/r/your-docker-username/ci-helloworld](https://hub.docker.com/r/wangcarpe/ci-helloworld)

## Notes
- Coverage drops will fail the pipeline as expected.
- The methods in `ArrayUtils` are tested with more than 90% coverage and 100% branch coverage.
```shell
coverage decreased from 94.9% to 45.7%
Failing the workflow run.
```
- we can see there if the coverage drops the pipeline will fail.
- you can see the two link
  - [a](https://github.com/Carpe-Wang/task3/actions/runs/14180516439)
    - my pipeline is success because the 
```shell
JaCoCo Test Coverage Summary
Coverage: 94.915%
Branches: 100.000%
Generated by: jacoco-badge-generator
```
  - [b](https://github.com/Carpe-Wang/task3/actions/runs/14180535477)
    - coverage decreased from 94.9% to 45.7% so it's failed.

### by the way
* because the .github is not visible so please see the detailed at GitHub repo.
* **If I include the task3 package now, I won't be able to submit it successfully because there are many files, and macOS automatically generates `.DS_Store` files, which causes the upload to fail. So I’ll have to trouble you to check the GitHub repository instead.**