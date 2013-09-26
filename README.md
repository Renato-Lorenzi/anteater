# anteater

![Travis CI](https://api.travis-ci.org/Renato-Lorenzi/anteater.png)

So, did you someday made an ant script?

If didn't, luck for you. If yes, then you saw in person that XML is not, how can we say this, **A PROGRAMMIG LANGUAGE**! Seriously, sorry apache, but who had this idea? We really hope that they had a good reason for it, but we didn't find it.

Then, after some discussions, lightning and thunders, the **anteater** was born! Enjoy programming ant scripts with a real script language!

So, having in mind that ant script in general are used to build Java solutions, what is the most used script language in Java island? Think a little more... Yes, *javascript*. Seems fair enough, doesn't it?

Then, with anteater you can merge the good features of ant, like filesets, with the powerfull of js language.

## Using

So, you may be asking how do we do this. It's pretty more simple that it looks. Keep reading for have an explanation of the main ant features that are already supported.

### Ant tasks

All the ant loaded tasks are accessible by the *ant* variable in anteater. For example, the following task:

```xml
<mkdir dir="./here"/>
```

Can be done right this in anteater:

```javascript
ant.mkdir({dir: "./here"});
```

Yes, the parameters are passed through JSON :smiley:.

So, in ant is common to have inner elements like:

```xml
<jar destfile="./lib/app.jar">
  <fileset dir="./classes" excludes="**/Test.class"/>
  <fileset dir="./resources">
    <exclude dir="./bin"/>
  </fileset>
</jar>
```

In anteater it can be done right this:

```javascript
ant.jar({
  destfile: "./lib/app.jar",
  fileset: [
    {dir: "./classes", excludes: "**/Test.class"},
    {dir: "./resources", exclude: {dir: "./bin"}}
  ]
});
```

So, probably you've realized that here's our first barrier, we cannot make two JSON objects with the same id. Then, for use this resource, you must use an array of objects, like above. What, in our opinion, is much better than in ant.

### Macrodefs and <sequential> elements

Then, probably you're asking yourself: and what about macrodefs? So we answer you, macrodefs no more please, you can do it pretty better: functions. Yep, you can use functions, as any other resource of js language in your scripts. For example, the macrodef:

```xml
<macrodef name="compile">
  <attribute name="project" />
  <sequential>
    <javac srcdir="@{project}/src"
         destdir="@{project}/bin"
         classpath="xyz.jar"
         debug="on"
         source="1.4"/>
  </sequential>
</macrodef>
```

Can be done right this:

```javascript
function compile(project) {
  var srcDir = project + "/src";
  var destDir = project + "/bin";
  ant.javac({
    srcdir: srcDir,
    destdir: destDir,
    classpath: "xyz.jar",
    debug: "on",
    source: "1.4"
  });
}
```

First, if you saw the var declaration and are asking yourself if you can use every js language feature, the answer is **YES, WE CAN!**

So, now about the function, to call it is pretty more simple too. Since to call macrodef you must call a taskdef loaded to the ant context, like `<compile project="xalala" />` in ant, what doing a js function must be done just calling `compile("xalala")`.

### Targets

Targets is a nice feature of ant right? Modularize your builds and specify what to execute is pretty awesome feature. And you can do it as well:

```xml
<target name="build">
  <mkdir dir="./xororo" />
</target>
```

Must be done with:

```javascript
ant.target({
    name: "build"
  }, function(){
    ant.mkdir({dir: "xororo"});
  });
```

### JS advantages

So, the most awesome advantage of programming the build script with js, is use the powerfull of this language. Yes, all the features can be utilized!

You don't need more that *ant-contrib* lib for doing that basic programming language features like **if** and **for**. You can create local variables! They doesn't need to be properties and make everything go to the ant lib context!

And obviously, you can make those *"complex"* things in the script and keep the script easy to read.

So enjoy it!

## Contributors

Check it out [here](https://github.com/Renato-Lorenzi/anteater/contributors)!

## License

The MIT License (MIT)

Copyright (c) [year] [fullname]

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
