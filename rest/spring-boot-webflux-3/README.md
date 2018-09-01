Spring WebFlux gives you two different styles: annotations and functional.

Depending on the type of application that you'd like to build, the constraints that you have to deal with - one or the other might be more relevant. You can even mix both in the same application.

The annotation-based model is very successful, but it also comes with a few limitations, mostly because of Java annotations themselves:

- the code path is not always clear, unless you know the internals of Spring Framework (do you know where handler mappings are detected? matched against incoming requests?)
- it's using reflection, which has a cost
- it can be hard to debug and extend
 
The functional variant tries to fix those issues and embrace a functional style (with the JDK8 function API) and immutability. It's got a "more library; less framework" touch to it, meaning that you're more in control of things. Here's an example: with RouterFunction, you can chain RequestPredicates and they are executed in order, so you're in full control of what ultimately handles the incoming request. With the annotations model, the most specific handler will be selected, by looking at the annotations on the method and the incoming request.

If you're perfectly happy with the annotations model, there's no reason to switch. But again, you can mix both and maybe you'll find the functional model handy. In my opinion, trying it even if you don't plan on adopting it won't hurt - worst case scenario this will broaden a bit your perspective as a developer and show you a different way of doing things.

For more on that, you can check out [Arjen Poutsma's talk on the functional web framework in Spring WebFlux](https://www.youtube.com/watch?v=upFFlGq5-NU).