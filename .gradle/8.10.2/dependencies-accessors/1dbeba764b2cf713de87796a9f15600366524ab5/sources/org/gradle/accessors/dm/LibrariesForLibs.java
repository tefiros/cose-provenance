package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final ComLibraryAccessors laccForComLibraryAccessors = new ComLibraryAccessors(owner);
    private final OrgLibraryAccessors laccForOrgLibraryAccessors = new OrgLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Group of libraries at <b>com</b>
     */
    public ComLibraryAccessors getCom() {
        return laccForComLibraryAccessors;
    }

    /**
     * Group of libraries at <b>org</b>
     */
    public OrgLibraryAccessors getOrg() {
        return laccForOrgLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class ComLibraryAccessors extends SubDependencyFactory {
        private final ComAugustcellarsLibraryAccessors laccForComAugustcellarsLibraryAccessors = new ComAugustcellarsLibraryAccessors(owner);
        private final ComGoogleLibraryAccessors laccForComGoogleLibraryAccessors = new ComGoogleLibraryAccessors(owner);

        public ComLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.augustcellars</b>
         */
        public ComAugustcellarsLibraryAccessors getAugustcellars() {
            return laccForComAugustcellarsLibraryAccessors;
        }

        /**
         * Group of libraries at <b>com.google</b>
         */
        public ComGoogleLibraryAccessors getGoogle() {
            return laccForComGoogleLibraryAccessors;
        }

    }

    public static class ComAugustcellarsLibraryAccessors extends SubDependencyFactory {
        private final ComAugustcellarsCoseLibraryAccessors laccForComAugustcellarsCoseLibraryAccessors = new ComAugustcellarsCoseLibraryAccessors(owner);

        public ComAugustcellarsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.augustcellars.cose</b>
         */
        public ComAugustcellarsCoseLibraryAccessors getCose() {
            return laccForComAugustcellarsCoseLibraryAccessors;
        }

    }

    public static class ComAugustcellarsCoseLibraryAccessors extends SubDependencyFactory {
        private final ComAugustcellarsCoseCoseLibraryAccessors laccForComAugustcellarsCoseCoseLibraryAccessors = new ComAugustcellarsCoseCoseLibraryAccessors(owner);

        public ComAugustcellarsCoseLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.augustcellars.cose.cose</b>
         */
        public ComAugustcellarsCoseCoseLibraryAccessors getCose() {
            return laccForComAugustcellarsCoseCoseLibraryAccessors;
        }

    }

    public static class ComAugustcellarsCoseCoseLibraryAccessors extends SubDependencyFactory {

        public ComAugustcellarsCoseCoseLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>java</b> with <b>com.augustcellars.cose:cose-java</b> coordinates and
         * with version reference <b>com.augustcellars.cose.cose.java</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJava() {
            return create("com.augustcellars.cose.cose.java");
        }

    }

    public static class ComGoogleLibraryAccessors extends SubDependencyFactory {
        private final ComGoogleCodeLibraryAccessors laccForComGoogleCodeLibraryAccessors = new ComGoogleCodeLibraryAccessors(owner);

        public ComGoogleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.google.code</b>
         */
        public ComGoogleCodeLibraryAccessors getCode() {
            return laccForComGoogleCodeLibraryAccessors;
        }

    }

    public static class ComGoogleCodeLibraryAccessors extends SubDependencyFactory {
        private final ComGoogleCodeGsonLibraryAccessors laccForComGoogleCodeGsonLibraryAccessors = new ComGoogleCodeGsonLibraryAccessors(owner);

        public ComGoogleCodeLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>com.google.code.gson</b>
         */
        public ComGoogleCodeGsonLibraryAccessors getGson() {
            return laccForComGoogleCodeGsonLibraryAccessors;
        }

    }

    public static class ComGoogleCodeGsonLibraryAccessors extends SubDependencyFactory {

        public ComGoogleCodeGsonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>gson</b> with <b>com.google.code.gson:gson</b> coordinates and
         * with version reference <b>com.google.code.gson.gson</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGson() {
            return create("com.google.code.gson.gson");
        }

    }

    public static class OrgLibraryAccessors extends SubDependencyFactory {
        private final OrgApacheLibraryAccessors laccForOrgApacheLibraryAccessors = new OrgApacheLibraryAccessors(owner);
        private final OrgBouncycastleLibraryAccessors laccForOrgBouncycastleLibraryAccessors = new OrgBouncycastleLibraryAccessors(owner);
        private final OrgJdomLibraryAccessors laccForOrgJdomLibraryAccessors = new OrgJdomLibraryAccessors(owner);

        public OrgLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.apache</b>
         */
        public OrgApacheLibraryAccessors getApache() {
            return laccForOrgApacheLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.bouncycastle</b>
         */
        public OrgBouncycastleLibraryAccessors getBouncycastle() {
            return laccForOrgBouncycastleLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.jdom</b>
         */
        public OrgJdomLibraryAccessors getJdom() {
            return laccForOrgJdomLibraryAccessors;
        }

    }

    public static class OrgApacheLibraryAccessors extends SubDependencyFactory {
        private final OrgApacheSantuarioLibraryAccessors laccForOrgApacheSantuarioLibraryAccessors = new OrgApacheSantuarioLibraryAccessors(owner);

        public OrgApacheLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.apache.santuario</b>
         */
        public OrgApacheSantuarioLibraryAccessors getSantuario() {
            return laccForOrgApacheSantuarioLibraryAccessors;
        }

    }

    public static class OrgApacheSantuarioLibraryAccessors extends SubDependencyFactory {

        public OrgApacheSantuarioLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>xmlsec</b> with <b>org.apache.santuario:xmlsec</b> coordinates and
         * with version reference <b>org.apache.santuario.xmlsec</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getXmlsec() {
            return create("org.apache.santuario.xmlsec");
        }

    }

    public static class OrgBouncycastleLibraryAccessors extends SubDependencyFactory {
        private final OrgBouncycastleBcpkixLibraryAccessors laccForOrgBouncycastleBcpkixLibraryAccessors = new OrgBouncycastleBcpkixLibraryAccessors(owner);

        public OrgBouncycastleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.bouncycastle.bcpkix</b>
         */
        public OrgBouncycastleBcpkixLibraryAccessors getBcpkix() {
            return laccForOrgBouncycastleBcpkixLibraryAccessors;
        }

    }

    public static class OrgBouncycastleBcpkixLibraryAccessors extends SubDependencyFactory {

        public OrgBouncycastleBcpkixLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jdk18on</b> with <b>org.bouncycastle:bcpkix-jdk18on</b> coordinates and
         * with version reference <b>org.bouncycastle.bcpkix.jdk18on</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJdk18on() {
            return create("org.bouncycastle.bcpkix.jdk18on");
        }

    }

    public static class OrgJdomLibraryAccessors extends SubDependencyFactory {

        public OrgJdomLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>jdom2</b> with <b>org.jdom:jdom2</b> coordinates and
         * with version reference <b>org.jdom.jdom2</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJdom2() {
            return create("org.jdom.jdom2");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final ComVersionAccessors vaccForComVersionAccessors = new ComVersionAccessors(providers, config);
        private final OrgVersionAccessors vaccForOrgVersionAccessors = new OrgVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com</b>
         */
        public ComVersionAccessors getCom() {
            return vaccForComVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org</b>
         */
        public OrgVersionAccessors getOrg() {
            return vaccForOrgVersionAccessors;
        }

    }

    public static class ComVersionAccessors extends VersionFactory  {

        private final ComAugustcellarsVersionAccessors vaccForComAugustcellarsVersionAccessors = new ComAugustcellarsVersionAccessors(providers, config);
        private final ComGoogleVersionAccessors vaccForComGoogleVersionAccessors = new ComGoogleVersionAccessors(providers, config);
        public ComVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.augustcellars</b>
         */
        public ComAugustcellarsVersionAccessors getAugustcellars() {
            return vaccForComAugustcellarsVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.com.google</b>
         */
        public ComGoogleVersionAccessors getGoogle() {
            return vaccForComGoogleVersionAccessors;
        }

    }

    public static class ComAugustcellarsVersionAccessors extends VersionFactory  {

        private final ComAugustcellarsCoseVersionAccessors vaccForComAugustcellarsCoseVersionAccessors = new ComAugustcellarsCoseVersionAccessors(providers, config);
        public ComAugustcellarsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.augustcellars.cose</b>
         */
        public ComAugustcellarsCoseVersionAccessors getCose() {
            return vaccForComAugustcellarsCoseVersionAccessors;
        }

    }

    public static class ComAugustcellarsCoseVersionAccessors extends VersionFactory  {

        private final ComAugustcellarsCoseCoseVersionAccessors vaccForComAugustcellarsCoseCoseVersionAccessors = new ComAugustcellarsCoseCoseVersionAccessors(providers, config);
        public ComAugustcellarsCoseVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.augustcellars.cose.cose</b>
         */
        public ComAugustcellarsCoseCoseVersionAccessors getCose() {
            return vaccForComAugustcellarsCoseCoseVersionAccessors;
        }

    }

    public static class ComAugustcellarsCoseCoseVersionAccessors extends VersionFactory  {

        public ComAugustcellarsCoseCoseVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.augustcellars.cose.cose.java</b> with value <b>1.1.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJava() { return getVersion("com.augustcellars.cose.cose.java"); }

    }

    public static class ComGoogleVersionAccessors extends VersionFactory  {

        private final ComGoogleCodeVersionAccessors vaccForComGoogleCodeVersionAccessors = new ComGoogleCodeVersionAccessors(providers, config);
        public ComGoogleVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.google.code</b>
         */
        public ComGoogleCodeVersionAccessors getCode() {
            return vaccForComGoogleCodeVersionAccessors;
        }

    }

    public static class ComGoogleCodeVersionAccessors extends VersionFactory  {

        private final ComGoogleCodeGsonVersionAccessors vaccForComGoogleCodeGsonVersionAccessors = new ComGoogleCodeGsonVersionAccessors(providers, config);
        public ComGoogleCodeVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.com.google.code.gson</b>
         */
        public ComGoogleCodeGsonVersionAccessors getGson() {
            return vaccForComGoogleCodeGsonVersionAccessors;
        }

    }

    public static class ComGoogleCodeGsonVersionAccessors extends VersionFactory  {

        public ComGoogleCodeGsonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>com.google.code.gson.gson</b> with value <b>2.10.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGson() { return getVersion("com.google.code.gson.gson"); }

    }

    public static class OrgVersionAccessors extends VersionFactory  {

        private final OrgApacheVersionAccessors vaccForOrgApacheVersionAccessors = new OrgApacheVersionAccessors(providers, config);
        private final OrgBouncycastleVersionAccessors vaccForOrgBouncycastleVersionAccessors = new OrgBouncycastleVersionAccessors(providers, config);
        private final OrgJdomVersionAccessors vaccForOrgJdomVersionAccessors = new OrgJdomVersionAccessors(providers, config);
        public OrgVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.apache</b>
         */
        public OrgApacheVersionAccessors getApache() {
            return vaccForOrgApacheVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.bouncycastle</b>
         */
        public OrgBouncycastleVersionAccessors getBouncycastle() {
            return vaccForOrgBouncycastleVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.jdom</b>
         */
        public OrgJdomVersionAccessors getJdom() {
            return vaccForOrgJdomVersionAccessors;
        }

    }

    public static class OrgApacheVersionAccessors extends VersionFactory  {

        private final OrgApacheSantuarioVersionAccessors vaccForOrgApacheSantuarioVersionAccessors = new OrgApacheSantuarioVersionAccessors(providers, config);
        public OrgApacheVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.apache.santuario</b>
         */
        public OrgApacheSantuarioVersionAccessors getSantuario() {
            return vaccForOrgApacheSantuarioVersionAccessors;
        }

    }

    public static class OrgApacheSantuarioVersionAccessors extends VersionFactory  {

        public OrgApacheSantuarioVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.apache.santuario.xmlsec</b> with value <b>4.0.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getXmlsec() { return getVersion("org.apache.santuario.xmlsec"); }

    }

    public static class OrgBouncycastleVersionAccessors extends VersionFactory  {

        private final OrgBouncycastleBcpkixVersionAccessors vaccForOrgBouncycastleBcpkixVersionAccessors = new OrgBouncycastleBcpkixVersionAccessors(providers, config);
        public OrgBouncycastleVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.bouncycastle.bcpkix</b>
         */
        public OrgBouncycastleBcpkixVersionAccessors getBcpkix() {
            return vaccForOrgBouncycastleBcpkixVersionAccessors;
        }

    }

    public static class OrgBouncycastleBcpkixVersionAccessors extends VersionFactory  {

        public OrgBouncycastleBcpkixVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.bouncycastle.bcpkix.jdk18on</b> with value <b>1.77</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJdk18on() { return getVersion("org.bouncycastle.bcpkix.jdk18on"); }

    }

    public static class OrgJdomVersionAccessors extends VersionFactory  {

        public OrgJdomVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.jdom.jdom2</b> with value <b>2.0.6.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getJdom2() { return getVersion("org.jdom.jdom2"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
