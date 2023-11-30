# `simpletest`: A simple framework for functional/performance tests (JDK 11)

## Functional testing

```java
public class MainCAS {

    public static void main(final String... args) throws Exception {
        new Execution(5, () -> {
            return OR(
                    AND(new CasLogin(), new CasValidate()),
                    CLEAR_CONTEXT
            );
        }).launch();
    }
}
```

Output:

```shell
Functional execution started: 1 thread, 5 iterations per thread
## iteration: 1
Clear context

## iteration: 2
GET: http://localhost:8080/cas/login?service=http%3A%2F%2Flocalhost%3A8081%2F
Execute => 200
POST: http://localhost:8080/cas/login?service=http%3A%2F%2Flocalhost%3A8081%2F
Execute => 302
Found: TGC=TGT-12-NYaEy2qNpHn6tHqLm4ZNNlNnp-5PNbKkc2TBICYeDZVeNssrxfXFWmb-9JuqcZNqzTQ-castest
GET: http://localhost:8080/cas/p3/serviceValidate?service=http%3A%2F%2Flocalhost%3A8081%2F&ticket=ST-16-Vl2W4k04TIlT5nNPbkenZj35Bhw-castest
Execute => 200

## iteration: 3
Re-use: TGC=TGT-12-NYaEy2qNpHn6tHqLm4ZNNlNnp-5PNbKkc2TBICYeDZVeNssrxfXFWmb-9JuqcZNqzTQ-castest
GET: http://localhost:8080/cas/login?service=http%3A%2F%2Flocalhost%3A8081%2F
Execute => 302
GET: http://localhost:8080/cas/p3/serviceValidate?service=http%3A%2F%2Flocalhost%3A8081%2F&ticket=ST-17-X3d3gdctk8VJjPvoZgj8o8laa70-castest
Execute => 200

## iteration: 4
Re-use: TGC=TGT-12-NYaEy2qNpHn6tHqLm4ZNNlNnp-5PNbKkc2TBICYeDZVeNssrxfXFWmb-9JuqcZNqzTQ-castest
GET: http://localhost:8080/cas/login?service=http%3A%2F%2Flocalhost%3A8081%2F
Execute => 302
GET: http://localhost:8080/cas/p3/serviceValidate?service=http%3A%2F%2Flocalhost%3A8081%2F&ticket=ST-18-VlYt-QKAZ7IjuFOiUIRnoyY7rpI-castest
Execute => 200

## iteration: 5
Clear context

Execution ended and took: 304 ms
```

## Performance testing

```java
public class Main {

    public static void main(final String... args) throws Exception {
        new Execution(3, 1000, CasLogin::new).launch();
    }
}
```

Output:

```shell
Performance execution started: 6 threads, 2000 iterations per thread
<fbeacdbefacdefbacdebfacdebfacdfebaFBEcACdDfebacdfebacdfebacdfebacdfebacdfebFacEBdfACeDbacdfebacdfebadcfbeadcfbeacdbfeacBFdbfEAeCaDcdbfeacdbfeacdbfeacdfbeacdfbeacdfFbBeEaAcCdDfbeacbfedacbfedacbfedacbfedacbfedBFEaAcbfeCdaDcbefdacbefdacbefdacbefdacbefdaBEFcbAefdCaDcbefdacbfedacbefdacbefdacbdfeacBFEbfedACacDbdfeacbdfeacbdfeacbfdeacdfbeacdfFbBeEcCaAdD>
Execution ended and took: 9 s
```
