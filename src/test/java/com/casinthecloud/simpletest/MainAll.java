package com.casinthecloud.simpletest;

public class MainAll {

    public static void main(final String... args) throws Exception {
        MainCAS.main(args);
        MainOIDC.main(args);
        MainSAML.main(args);

        MainCASDelegateCAS.main(args);
        MainCASDelegateOIDC.main(args);
        MainCASDelegateSAML.main(args);

        MainOIDCDelegateCAS.main(args);
        MainOIDCDelegateOIDC.main(args);
        MainOIDCDelegateSAML.main(args);

        MainSAMLDelegateCAS.main(args);
        MainSAMLDelegateOIDC.main(args);
        MainSAMLDelegateSAML.main(args);
    }
}
