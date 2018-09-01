Spring WebFlux gives you two different styles: annotations and functional.

Depending on the type of application that you'd like to build, the constraints that you have to deal with - one or the other might be more relevant. You can even mix both in the same application.

The annotation-based model is very successful, but it also comes with a few limitations, mostly because of Java annotations themselves:

- the code path is not always clear
- it's using reflection, which has a cost
- it can be hard to debug and extend
 
The functional variant tries to fix those issues and embrace a functional style (with the JDK8 function API) and immutability. It's got a "more library; less framework" touch to it, meaning that you're more in control of things. 

For more on that, you can check out [Arjen Poutsma's talk on the functional web framework in Spring WebFlux](https://www.youtube.com/watch?v=upFFlGq5-NU).