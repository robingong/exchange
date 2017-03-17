/*
 * This file is part of bisq.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.locale;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CountryUtil {
    private static final Logger log = LoggerFactory.getLogger(CountryUtil.class);
    private static Locale defaultLocale;

    public static List<Country> getAllSepaEuroCountries(Locale locale) {
        List<Country> list = new ArrayList<>();
        String[] codes = {"AT", "BE", "CY", "DE", "EE", "FI", "FR", "GR", "IE",
                "IT", "LV", "LT", "LU", "MC", "MT", "NL", "PT", "SK", "SI", "ES"};
        populateCountryListByCodes(list, codes, locale);
        list.sort((a, b) -> a.name.compareTo(b.name));

        return list;
    }

    private static void populateCountryListByCodes(List<Country> list, String[] codes) {

    }

    private static void populateCountryListByCodes(List<Country> list, String[] codes, Locale defaultLocale) {
        for (String code : codes) {
            Locale locale = new Locale(LanguageUtil.getDefaultLanguage(defaultLocale), code, "");
            String regionCode = getRegionCode(locale.getCountry());
            final Region region = new Region(regionCode, getRegionName(regionCode));
            final Country country = new Country(locale.getCountry(), locale.getDisplayCountry(), region);
            list.add(country);
        }
    }

    public static boolean containsAllSepaEuroCountries(List<String> countryCodesToCompare, Locale locale) {
        countryCodesToCompare.sort(String::compareTo);
        List<String> countryCodesBase = getAllSepaEuroCountries(locale).stream().map(c -> c.code).collect(Collectors.toList());
        return countryCodesToCompare.toString().equals(countryCodesBase.toString());
    }

    public static List<Country> getAllSepaNonEuroCountries(Locale locale) {
        List<Country> list = new ArrayList<>();
        String[] codes = {"BG", "HR", "CZ", "DK", "GB", "HU", "PL", "RO",
                "SE", "IS", "NO", "LI", "CH"};
        populateCountryListByCodes(list, codes, locale);
        list.sort((a, b) -> a.name.compareTo(b.name));
        return list;
    }

    public static List<Country> getAllSepaCountries(Locale locale) {
        List<Country> list = new ArrayList<>();
        list.addAll(getAllSepaEuroCountries(locale));
        list.addAll(getAllSepaNonEuroCountries(locale));
        return list;
    }

    public static Country getDefaultCountry(final Locale locale) {
        String regionCode = getRegionCode(locale.getCountry());
        final Region region = new Region(regionCode, getRegionName(regionCode));
        return new Country(locale.getCountry(), locale.getDisplayCountry(), region);
    }

    public static String getNameByCode(String countryCode) {
        return new Locale(LanguageUtil.getDefaultLanguage(defaultLocale), countryCode).getDisplayCountry();
    }

    public static String getNameByCode(String countryCode, Locale locale) {
        return new Locale(LanguageUtil.getDefaultLanguage(locale), countryCode).getDisplayCountry();
    }

    public static String getNameAndCode(String countryCode) {
        return getNameByCode(countryCode, defaultLocale);
    }

    public static String getNameAndCode(String countryCode, Locale locale) {
        return getNameByCode(countryCode, locale) + " (" + countryCode + ")";
    }

    public static String getCodesString(List<String> countryCodes) {
        return countryCodes.stream().collect(Collectors.joining(", "));
    }

    public static String getNamesByCodesString(List<String> countryCodes, Locale locale) {
        return getNamesByCodes(countryCodes, locale).stream().collect(Collectors.joining(",\n"));
    }

    public static List<Region> getAllRegions() {
        final List<Region> allRegions = new ArrayList<>();

        String regionCode = "NA";
        Region region = new Region(regionCode, getRegionName(regionCode));
        allRegions.add(region);

        regionCode = "SA";
        region = new Region(regionCode, getRegionName(regionCode));
        allRegions.add(region);

        regionCode = "AF";
        region = new Region(regionCode, getRegionName(regionCode));
        allRegions.add(region);

        regionCode = "EU";
        region = new Region(regionCode, getRegionName(regionCode));
        allRegions.add(region);

        regionCode = "AS";
        region = new Region(regionCode, getRegionName(regionCode));
        allRegions.add(region);

        regionCode = "OC";
        region = new Region(regionCode, getRegionName(regionCode));
        allRegions.add(region);

        return allRegions;
    }

    public static List<Country> getAllCountriesForRegion(Region selectedRegion) {
        return Lists.newArrayList(Collections2.filter(getAllCountries(), country ->
                selectedRegion != null && country != null && selectedRegion.equals(country.region)));
    }

    public static List<Country> getAllCountries() {
        final Set<Country> allCountries = new HashSet<>();
        for (final Locale locale : getAllCountryLocales()) {
            String regionCode = getRegionCode(locale.getCountry());
            final Region region = new Region(regionCode, getRegionName(regionCode));
            final Country country = new Country(locale.getCountry(), locale.getDisplayCountry(), region);
            allCountries.add(country);
        }

        allCountries.add(new Country("GE", "Georgia", new Region("AS", getRegionName("AS"))));
        allCountries.add(new Country("BW", "Botswana", new Region("AF", getRegionName("AF"))));

        final List<Country> allCountriesList = new ArrayList<>(allCountries);
        allCountriesList.sort((locale1, locale2) -> locale1.name.compareTo(locale2.name));
        return allCountriesList;
    }

    private static List<Locale> getAllCountryLocales() {
        List<Locale> allLocales = LocaleUtil.getAllLocales();

        // Filter duplicate locale entries 
        Set<Locale> allLocalesAsSet = allLocales.stream().filter(locale -> !locale.getCountry().isEmpty())
                .collect(Collectors.toSet());

        List<Locale> allCountryLocales = new ArrayList<>();
        allCountryLocales.addAll(allLocalesAsSet);
        allCountryLocales.sort((locale1, locale2) -> locale1.getDisplayCountry().compareTo(locale2.getDisplayCountry()));
        return allCountryLocales;
    }

    private static List<String> getNamesByCodes(List<String> countryCodes, Locale locale) {
        return countryCodes.stream().map(s -> CountryUtil.getNameByCode(s, locale)).collect(Collectors.toList());
    }

    // other source of countries: https://developers.braintreepayments.com/reference/general/countries/java
    private static final String[] countryCodes = new String[]{"AE", "AL", "AR", "AT", "AU", "BA", "BE", "BG", "BH",
            "BO", "BR", "BW", "BY", "CA", "CH", "CL", "CN", "CO", "CR", "CS", "CU", "CY", "CZ", "DE", "DK", "DO", "DZ",
            "EC", "EE", "EG", "ES", "FI", "FR", "GE", "GB", "GR", "GT", "HK", "HN", "HR", "HU", "ID", "IE", "IL", "IN",
            "IQ", "IS", "IT", "JO", "JP", "KE", "KH", "KR", "KW", "KZ", "LB", "LT", "LU", "LV", "LY", "MA", "MD", "ME", "MK", "MT", "MX",
            "MY", "NI", "NL", "NO", "NZ", "OM", "PA", "PE", "PH", "PL", "PR", "PT", "PY", "QA", "RO", "RS", "RU",
            "SA", "SD", "SE", "SG", "SI", "SK", "SV", "SY", "TH", "TN", "TR", "TW", "UA", "US", "UY", "VE", "VN",
            "YE", "ZA"};

    private static final List<String> countryCodeList = Arrays.asList(countryCodes);
    private static final String[] regionCodes = new String[]{"AS", "EU", "SA", "EU", "OC", "EU", "EU", "EU", "AS",
            "SA", "SA", "AF", "EU", "NA", "EU", "SA", "AS", "SA", "NA", "EU", "NA", "EU", "EU", "EU", "EU", "NA", "AF",
            "SA", "EU", "AF", "EU", "EU", "EU", "AS", "EU", "EU", "NA", "AS", "NA", "EU", "EU", "AS", "EU", "AS", "AS",
            "AS", "EU", "EU", "AS", "AS", "AF", "AS", "AS", "AS", "AS", "AS", "EU", "EU", "EU", "AF", "AF", "EU", "EU", "EU", "EU", "NA",
            "AS", "NA", "EU", "EU", "OC", "AS", "NA", "SA", "AS", "EU", "NA", "EU", "SA", "AS", "EU", "EU", "EU",
            "AS", "AF", "EU", "AS", "EU", "EU", "NA", "AS", "AS", "AF", "AS", "AS", "EU", "NA", "SA", "SA", "AS",
            "AS", "AF"};
    private static final List<String> regionCodeList = Arrays.asList(regionCodes);
    private static final String[][] regionCodeToName = new String[][]{
            {"NA", "North America"}, {"SA", "South America"}, {"AF", "Africa"}, {"EU", "Europe"}, {"AS", "Asia"},
            {"OC", "Oceania"}};

    private static String getRegionName(final String regionCode) {
        for (final String[] regionName : regionCodeToName) {
            if (regionName[0].equals(regionCode)) {
                return regionName[1];
            }
        }
        return regionCode;
    }

    private static String getRegionCode(String countryCode) {
        if (!countryCode.isEmpty() && countryCodeList.contains(countryCode)) {
            return regionCodeList.get(countryCodeList.indexOf(countryCode));
        } else {
            return "Undefined";
        }
    }

    public static String getDefaultCountryCode(Locale locale) {
        // might be set later in pref or config, so not use Preferences.getDefaultLocale() anywhere in the code
        return locale.getCountry();
    }

    public static void setDefaultLocale(Locale defaultLocale) {
        CountryUtil.defaultLocale = defaultLocale;
    }

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }
}