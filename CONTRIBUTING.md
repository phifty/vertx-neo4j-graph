## Contributor Guidelines

### Commit Checklist

Make sure to stick to our **Commit Checklist** or your changes most likely won't make it :)

Prior to each commit be sure to obey the n commandments of committing:

1. write a **human-readable :) [Changelog](http://raaftech.github.io/neo4vertx/changelog.html)** entry that describes your change
1. add **unit tests** (if it makes sense)
1. document your changes in the **maven site**
1. include this list in a your **pull request**

### Pull Request Rules

1. Create separate branches from which you create a pull request. This has the advantage that you don't pollute your pull request branch with multiple commits. 
Once a branch is polluted with many commits it is very hard to merge it. 

1. Add the commit checklist at the bottom to your pull request.

1. Avoid merge commits - try to keep your local branches clean and uptodate. Use **git rebase upstream/BRANCHNAME** if needed to stack your changes ontop of the HEAD revision of the upstream branch.

1. Don't rename merge commits. Let git handle those commits.
