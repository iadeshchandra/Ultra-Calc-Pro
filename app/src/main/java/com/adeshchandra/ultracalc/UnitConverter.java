package com.adeshchandra.ultracalc;

public class UnitConverter {
    public static class Cat {
        public final String name, emoji;
        public final String[] units;
        public final double[] base;
        Cat(String name, String emoji, String[] units, double[] base) {
            this.name=name; this.emoji=emoji; this.units=units; this.base=base;
        }
    }

    public static final Cat[] CATS = {
        new Cat("Length","📏",new String[]{"mm","cm","m","km","inch","foot","yard","mile","nautical mi","light year"},
            new double[]{0.001,0.01,1,1000,0.0254,0.3048,0.9144,1609.344,1852,9.461e15}),
        new Cat("Weight","⚖️",new String[]{"mg","g","kg","metric ton","lb","oz","stone","US ton","carat"},
            new double[]{1e-6,0.001,1,1000,0.453592,0.028349,6.35029,907.185,0.0002}),
        new Cat("Temperature","🌡️",new String[]{"Celsius","Fahrenheit","Kelvin","Rankine"},
            new double[]{1,1,1,1}),
        new Cat("Area","📐",new String[]{"mm²","cm²","m²","km²","in²","ft²","yd²","mile²","hectare","acre"},
            new double[]{1e-6,1e-4,1,1e6,6.4516e-4,0.092903,0.836127,2.59e6,10000,4046.86}),
        new Cat("Volume","🧪",new String[]{"mL","L","m³","in³","ft³","US gal","UK gal","US pint","US cup","fl oz","tbsp","tsp"},
            new double[]{0.001,1,1000,0.016387,28.3168,3.78541,4.54609,0.473176,0.236588,0.029574,0.014787,0.004929}),
        new Cat("Speed","🚀",new String[]{"m/s","km/h","mph","ft/s","knot","Mach"},
            new double[]{1,0.277778,0.44704,0.3048,0.514444,340.29}),
        new Cat("Time","⏱️",new String[]{"ms","second","minute","hour","day","week","month","year","decade","century"},
            new double[]{0.001,1,60,3600,86400,604800,2.628e6,3.156e7,3.156e8,3.156e9}),
        new Cat("Data","💾",new String[]{"bit","byte","KB","MB","GB","TB","PB"},
            new double[]{0.125,1,1024,1048576,1.0737e9,1.0995e12,1.1259e15}),
        new Cat("Pressure","🌬️",new String[]{"Pa","kPa","MPa","bar","atm","psi","torr","mmHg"},
            new double[]{1,1000,1e6,100000,101325,6894.76,133.322,133.322}),
        new Cat("Energy","⚡",new String[]{"J","kJ","MJ","cal","kcal","Wh","kWh","BTU","eV"},
            new double[]{1,1000,1e6,4.184,4184,3600,3.6e6,1055.06,1.602e-19}),
        new Cat("Power","🔋",new String[]{"W","kW","MW","HP","BTU/h","cal/s"},
            new double[]{1,1000,1e6,745.7,0.29307,4.184}),
        new Cat("Angle","📐",new String[]{"degree","radian","gradian","arcmin","arcsec","revolution"},
            new double[]{Math.PI/180,1,Math.PI/200,Math.PI/10800,Math.PI/648000,2*Math.PI}),
        new Cat("Fuel","⛽",new String[]{"km/L","L/100km","MPG(US)","MPG(UK)","mi/L"},
            new double[]{1,1,1,1,1}),
        new Cat("Currency","💰",new String[]{"USD","EUR","GBP","INR","JPY","CNY","AUD","CAD","CHF","SGD","AED","SAR","BDT","PKR","MYR","THB"},
            new double[]{1,1.08,1.27,0.012,0.0067,0.138,0.65,0.73,1.13,0.74,0.272,0.267,0.0091,0.0036,0.226,0.0275}),
    };

    public static String convert(int cat, double v, int from, int to) {
        if (CATS[cat].name.equals("Temperature")) return convertTemp(v, from, to);
        if (CATS[cat].name.equals("Fuel")) return convertFuel(v, from, to);
        double base = v * CATS[cat].base[from];
        double result = base / CATS[cat].base[to];
        if (result == Math.floor(result) && Math.abs(result) < 1e12) return String.valueOf((long)result);
        return String.format("%.8f", result).replaceAll("0+$","").replaceAll("\\.$","");
    }

    private static String convertTemp(double v, int from, int to) {
        double c; // to Celsius
        switch(from){case 0:c=v;break;case 1:c=(v-32)*5.0/9;break;case 2:c=v-273.15;break;default:c=(v-491.67)*5.0/9;}
        double r;
        switch(to){case 0:r=c;break;case 1:r=c*9.0/5+32;break;case 2:r=c+273.15;break;default:r=(c+273.15)*9.0/5;}
        return String.format("%.4f",r).replaceAll("0+$","").replaceAll("\\.$","");
    }
    private static String convertFuel(double v, int from, int to) {
        double kml; // to km/L
        switch(from){case 0:kml=v;break;case 1:kml=100.0/v;break;case 2:kml=v*0.425144;break;case 3:kml=v*0.354006;break;default:kml=v*1.60934;}
        double r;
        switch(to){case 0:r=kml;break;case 1:r=100.0/kml;break;case 2:r=kml/0.425144;break;case 3:r=kml/0.354006;break;default:r=kml/1.60934;}
        return String.format("%.4f",r).replaceAll("0+$","").replaceAll("\\.$","");
    }
}
