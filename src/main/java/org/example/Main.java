package org.example;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

  private static Random ourRundom = new Random();

  public static void main(String[] args) {

    // for collections we can call stream() method to create a Stream object out of it
    List<Integer> intList = List.of(1, 2, 3, 4, 5);
    Stream<Integer> intStream = intList.stream();
    System.out.println(intStream.count());

    // Stream.of
    Stream<Integer> intStream2 = Stream.of(1, 2, 3);
    Stream<Integer> singleElementStream = Stream.of(5);
    System.out.println(intStream2.count());
    System.out.println(singleElementStream.count());

    // empty Stream
    Stream<String> emptyStream = Stream.of();
    Stream<String> emptyStream2 = Stream.empty();
    System.out.println(emptyStream.count());
    System.out.println(emptyStream2.count());

    // sequence vs parallel
    Consumer<Integer> printElementConsumer = new Consumer<Integer>() {
      @Override
      public void accept(Integer integer) {
        System.out.println(integer + " " + Thread.currentThread().getName());
      }
    };

    System.out.println("Sequential stream: ");
    Stream.of(5, 6, 7).forEach(printElementConsumer);
    System.out.println("Parallel stream: ");
    Stream.of(5, 6, 7).parallel().forEach(printElementConsumer);

    // infinite streams
    Supplier<Integer> randomIntegerSupplier = new Supplier<Integer>() {
      @Override
      public Integer get() {
        return new Random().nextInt();
      }
    };
    // method reference syntax, in place of Consumer you can provide Consumer object, but also
    // any other void method (same type of method as accept method from Consumer)
    // method reference can be used as static (method is static, or the object is static)
    Stream.generate(randomIntegerSupplier).limit(20).forEach(System.out::println);
//    Stream.generate(ourRundom::nextInt).forEach(System.out::println);

    // generate 20 elements with iterate method
    UnaryOperator<Integer> increaseBy2UnaryOperator = new UnaryOperator<Integer>() {
      @Override
      public Integer apply(Integer integer) {
        return integer + 2;
      }
    };

    // Stream pipeline - stream operations we call on the stream until we call some terminal operation
    // iterate is creation operation (all such operations are intermediate), you can call other operations after this one
    Stream<Integer> infiniteIntegers = Stream.iterate(10, increaseBy2UnaryOperator);
    // limit is intermediate operation, you can call multiple intermediate operations by using the result stream of previous opeations
    // you can't call two operations on the same stream
    Stream<Integer> twentyIntegers = infiniteIntegers.limit(20);
    // calling forEach terminal operations will close the stream, pipeline is finished, stream closed
    // we can use only single terminal operation in pipeline
    // pipeline will be executed when a terminal operation is called
    twentyIntegers.forEach(System.out::println);
    // trying to call count terminal operation on closed stream, will get exception
//    System.out.println(twentyIntegers.count());

    // generate 20 elements with iterate method using predicate as stopping mechanism
    Predicate<Integer> limitTo20ElementsPredicate = new Predicate<Integer>() {
      @Override
      public boolean test(Integer integer) {
        return integer <= 48;
      }
    };

    // preferred way how to use stream pipelines, with chaining
    Stream.iterate(10, limitTo20ElementsPredicate, increaseBy2UnaryOperator)
        .forEach(System.out::println);


    // Terminal operations, we can have only one of this kind in the pipeline
    System.out.println("Testing count with infinite stream: ");
    // count can't terminate infinite stream
//    System.out.println(Stream.iterate(0, increaseBy2UnaryOperator).count());

    // min/max
    Optional<Integer> maxOptional = intList.stream().max(Integer::compareTo);
    // functional approach to consume optional
    // with lambdas written in a single line
    Consumer<Integer> optionalConsumer = new Consumer<Integer>() {
      @Override
      public void accept(Integer integer) {
        System.out.println("Max value in intList is: " + integer);
      }
    };
    maxOptional.ifPresent(optionalConsumer);

    // object oriented approach to consume optional
    if (maxOptional.isPresent()) {
      System.out.println("Max value in intList is: " + maxOptional.get());
    }

    // findAny/findFirst - generally should be used with some filtering (in combination with filter method)
    System.out.println(Stream.iterate(10, increaseBy2UnaryOperator).findFirst().get());
    System.out.println(Stream.iterate(10, increaseBy2UnaryOperator).findAny().get());

    // allMatch/anyMatch/noneMatch
    Predicate<Integer> equals20Predicate = new Predicate<Integer>() {
      @Override
      public boolean test(Integer integer) {
        return integer.equals(20);
      }
    };
    System.out.println(Stream.iterate(10, increaseBy2UnaryOperator).anyMatch(equals20Predicate));

    // reduce
    BinaryOperator<Integer> intSumBO = new BinaryOperator<Integer>() {
      @Override
      public Integer apply(Integer integer, Integer integer2) {
        return integer + integer2;
      }
    };

    BinaryOperator<Integer> intProductBO = new BinaryOperator<Integer>() {
      @Override
      public Integer apply(Integer integer, Integer integer2) {
        return integer * integer2;
      }
    };

    // 1,2,3,4,5
    // 3,7,5
    // 10,5
    // 15
    System.out.println(intList.stream().reduce(intSumBO).get());
    System.out.println(intList.stream().reduce(intProductBO).get());

    // collect
    List<Integer> copyOfIntList = intList.stream().collect(Collectors.toList());
    intList.stream().forEach(System.out::println);
    copyOfIntList.stream().forEach(System.out::println);

    // Intermediate operations, we can have many of these in the pipeline (and can do chaining)
    // filter
    Predicate<Integer> evenIntegerPredicate = new Predicate<Integer>() {
      @Override
      public boolean test(Integer integer) {
        return integer % 2 == 0;
      }
    };
    List<Integer> evenIntegers = intList.stream().filter(evenIntegerPredicate).collect(Collectors.toList());
    System.out.println(evenIntegers);

    // distinct
    List<String> stringList = List.of("hello", "world", "hello", "duck", "whatever", "hello");
    Set<String> stringSet = stringList.stream().collect(Collectors.toSet());
    System.out.println(stringSet);
    List<String> noDuplicates = stringList.stream().distinct().collect(Collectors.toList());
    System.out.println(noDuplicates);

    // limit and skip
    UnaryOperator<Integer> increaseBy1UO = new UnaryOperator<Integer>() {
      @Override
      public Integer apply(Integer integer) {
        return integer + 1;
      }
    };
    Stream.iterate(1, increaseBy1UO).skip(5).limit(5).forEach(System.out::print);
  }
}