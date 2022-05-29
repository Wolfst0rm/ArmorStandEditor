## Contributing to ArmorStandEditor

We are happy that you are willing to contribute. Usually, we are very lenient with all PRs, but there are still some guidelies you can follow to make the approval process go smoother.

- Do you run a server and have experience in Development? Take a look at our ["Help Wanted"](https://github.com/Wolfieheart/ArmorStandEditor/labels/help%20wanted) and ["BUG-0: Pending Investigation"](https://github.com/Wolfieheart/ArmorStandEditor/labels/BUG-0%3A%20Pending%20Investigation) Issues,  where you can find issues that need extra testing and investigation.
- Do you speak multiple languages? If so, we always welcome contributions for our Language Files/Translations

## Contributing Code?
Code Contributions are always welcomed and are made via GitHub's Pull Request System. Make a Personal Fork of the ArmorStandEditor repository (this) and open it in your IDE of Choice.
You can ask for any information/help on your idea/contribution on the [ASE Discord](https://discord.com/invite/aUf4AWAKmr) in the #development channel.

- Please make sure to read our [License](https://github.com/Wolfieheart/ArmorStandEditor/blob/master/LICENSE.md) before contributing.  If you do not agree with the terms and conditions of our license, please do not contribute any code.
- By making a pull request, you represent that you have the right to waive copyright and related rights to your contribution, and agree that all copyright and related rights in your contributions are waived, and you acknowledge that we have the copyright to use and modify your contribution under our [License](https://github.com/Wolfieheart/ArmorStandEditor/blob/master/LICENSE.md) for perpetuity.

## Code requirements
- You must use Tabs (Size 4 Spaces).
- Keep your environment in Java 17. Github handles building all of this into Java 17 and 18, but keep in mind that, while you will be able to use modern language features that way (like patterns), some standard library methods added after jdk 8 will not work and cause "cannot find symbol" errors when building (like String.isBlank).
- Avoid Wildcard Imports (Preferred, but is allowed. Let me know if this can't be avoided on Discord.)
- Don't run IDE Formatters over entire class files, unless it's a new class-file you're implementing
    - This is done to avoid us changing the lines everytime, each IDE is different, so we don't have a constant here and there.
    -  This also keeps file compares on github simple.
- Reflection should be a last resort.
- Do not use NMS or CraftBukkit Code. Bukkit/Spigot API calls only.


## Contributing translations
Translations of ArmorStandEditor's Base Language File [English (US)](https://github.com/Wolfieheart/ArmorStandEditor/blob/master/src/main/resources/lang/en_US.yml) are welcomed and can be done following the Code Guidelines outlined above.

- Do not work on the en_US.yml base file, this gets overwritten when the plugin gets enabled. Please create a new language file
- Language files are stored in `resources/lang/<name_Here>.yml`
- Change the formatting codes at the start of a file as desired.
- Also ensure that you add your Language to the Main ArmorStandEditor Class. How to do that can be seen [here](https://github.com/Wolfieheart/ArmorStandEditor/blob/master/src/main/java/io/github/rypofalem/armorstandeditor/ArmorStandEditorPlugin.java#L177). Please add it and let us know on the Discord, so we know that you added it. We will handle the rest by working off your branch such as adding it to the Metrics tool.

## Making Any Changes
- Create a personal fork (not an org fork) of the ArmorStandEditor repository (this).
    - Name your branch something relevant (i.e: /dev/\<Version Here>/\<Feature Name Here>)
    - Work off the Development Branch unless already Merged into Main - This will therefore allow Developments to be merged together to 1 big branch
    - To create a branch in Git;
        - `git branch relevantBranchName`
        - Then checkout the new branch with `git checkout relevantBranchName`
    - Check for unnecessary whitespace with `git diff --check` before committing.