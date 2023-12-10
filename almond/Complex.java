package almond;

import java.text.DecimalFormat;

class Polar {
    private final static DecimalFormat formatter = new DecimalFormat("#.##");
    public final double polar_length;
    public final double polar_angle;

    public Polar(double polar_length, double polar_angle_rad) {
        if (polar_angle_rad > 2 * Math.PI)
            System.err.println("WARNING: Angle too large: " + polar_angle_rad + ". Polar angle must be <= 2*PI.");
        this.polar_length = polar_length;
        this.polar_angle = polar_angle_rad;
    }

    public Polar(Coordinate coordinate) {
        this.polar_length = Math.sqrt(Math.pow(coordinate.real, 2) + Math.pow(coordinate.imaginary, 2));
        this.polar_angle = Math.atan(coordinate.imaginary / coordinate.real);
    }

    @Override
    public String toString() {
        return "(" + formatter.format(this.polar_length) + "*E(" + formatter.format(Math.toDegrees(this.polar_angle))
                + "°))";
    }
}

class Coordinate {
    private final static DecimalFormat formatter = new DecimalFormat("#.##");
    public final double real;
    public final double imaginary;

    public Coordinate(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public Coordinate(Polar polar) {
        this.real = polar.polar_length * Math.cos(polar.polar_angle);
        this.imaginary = polar.polar_length * Math.sin(polar.polar_angle);
    }

    @Override
    public String toString() {
        return "(" +
            formatter.format(this.real) +
            (this.imaginary >= 0 ? "+" : "") +
            formatter.format(this.imaginary) +
            "i)";
    }
}

/**
 * Class for dealing with complex numbers.
 * 
 * Numbers can be represented as coordinates or polar coordinates.
 * Conversion automatically occurs when needed.
 * 
 * Methods that may convert one or both operands are marked
 * with {@code @converts <form> -> <to>}.
 */
public class Complex {
    private Coordinate coordinate;
    private Polar polar;

    /**
     * Create a complex number from polar coordinate.
     */
    public Complex(Polar polar) {
        this.coordinate = null;
        this.polar = polar;
    }

    /**
     * Create a complex number from coordinate.
     */
    public Complex(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.polar = null;
    }

    /**
     * Create a complex number from rational number where the real number is the
     * real part of the
     * resulting complex number and the imaginary part is zero.
     * 
     * @param r Rational number.
     */
    public Complex(double r) {
        this.coordinate = new Coordinate(r, 0);
        this.polar = new Polar(Math.abs(r), r > 0 ? 0 : -Math.PI);
    }

    @Override
    public String toString() {
        if (coordinate != null)
            return coordinate.toString();
        else
            return polar.toString();
    }

    public boolean hasCoordinate() {
        return coordinate != null;
    }

    /**
     * Get the complex number as coordinate.
     * 
     * @converts Polar -> Coordinate
     */
    public Coordinate getCoordinate() {
        if (hasCoordinate())
            return coordinate;
        else
            coordinate = new Coordinate(polar);
        return coordinate;
    }

    public boolean hasPolar() {
        return polar != null;
    }

    /**
     * Get the complex number as polar coordinate.
     * 
     * @converts Coordinate -> Polar
     */
    public Polar getPolar() {
        if (hasPolar())
            return polar;
        else
            polar = new Polar(coordinate);
        return polar;
    }

    /**
     * Get absolute value (distance from complex plane origin) of the complex
     * number.
     * 
     * Tip: If you only need to check a value, consider using {@code Complex.sqAbs},
     * which avoids the square root and conversion to polar coordinate.
     * 
     * @converts Coordinate -> Polar
     */
    public double abs() {
        return getPolar().polar_length;
    }

    /**
     * Get square of absolute value.
     * 
     * This is achieved without converting to polar coordinates or square roots.
     * 
     * @converts Polar -> Coordinate
     */
    public double sqAbs() {
        Coordinate c = getCoordinate();
        return Math.pow(c.real, 2) + Math.pow(c.imaginary, 2);
    }

    /**
     * Get the complex conjugate of the complex number.
     * 
     * @converts Polar -> Coordinate
     */
    public Complex getConjugate() {
        Coordinate c = getCoordinate();
        return new Complex(new Coordinate(c.real, -1 * c.imaginary));
    }

    /**
     * Returns the sum of two complex numbers.
     * 
     * @param z Second complex number.
     * @return {@code z_1 + z_2}
     * @converts Polar -> Coordinate
     */
    public Complex add(Complex z) {
        Coordinate c1 = getCoordinate();
        Coordinate c2 = z.getCoordinate();
        return new Complex(
                new Coordinate(
                        c1.real + c2.real,
                        c1.imaginary + c2.imaginary));
    }

    /**
     * Returns the difference of two complex numbers.
     * 
     * @param z Second complex number.
     * @return {@code z_1 - z_2}
     * @converts Polar -> Coordinate
     */
    public Complex sub(Complex z) {
        Coordinate c1 = getCoordinate();
        Coordinate c2 = z.getCoordinate();
        return new Complex(
                new Coordinate(
                        c1.real - c2.real,
                        c1.imaginary - c2.imaginary));
    }

    /**
     * Returns the product of two complex numbers.
     * 
     * @param z Second complex number.
     * @return {@code z_1 * z_2}
     * @converts Polar -> Coordinate (when z_1 and z_2 are not in same representation)
     */
    public Complex mul(Complex z) {
        if (hasPolar() && z.hasPolar()) {
            Polar p1 = getPolar();
            Polar p2 = z.getPolar();
            return new Complex(
                    new Polar(
                            p1.polar_length * p2.polar_length,
                            (p1.polar_angle + p2.polar_angle) % (2 * Math.PI)));
        } else {
            Coordinate c1 = getCoordinate();
            Coordinate c2 = z.getCoordinate();
            // (a+bi)*(c+di)
            // ac+iad+ibc-bd
            // ac-bd +i(ad+bc)
            return new Complex(
                    new Coordinate(
                            c1.real * c2.real - c1.imaginary * c2.imaginary,
                            (c1.real * c2.imaginary + c1.imaginary * c2.real)));
        }
    }

    /**
     * Returns the quotient of two complex numbers.
     * 
     * @param z Second complex number.
     * @return {@code z_1 / z_2}
     * @converts Coordinate -> Polar
     */
    public Complex div(Complex z) {
        Polar p1 = getPolar();
        Polar p2 = z.getPolar();
        return new Complex(
                new Polar(
                        p1.polar_length / p2.polar_length,
                        (p1.polar_angle - p2.polar_angle) % (2 * Math.PI)));
    }

    /**
     * Returns the complex number raised to the {@code power}th power.
     *
     * @param power The power to raise the complex number to.
     * @return {@code z ^ power}
     * @converts Coordinate -> Polar
     */
    public Complex pow(double power) {
        Polar p = getPolar();
        return new Complex(
                new Polar(
                        Math.pow(p.polar_length, power),
                        (p.polar_angle * power) % (2 * Math.PI)));
    }

    /**
     * Returns the square of the complex number without converting to polar
     * coordinates.
     *
     * @return {@code z ^ 2}
     * @converts Polar -> Coordinate
     */
    public Complex squared() {
        Coordinate c = getCoordinate();
        // (a+bi)^2
        // a^2 + 2abi + (bi)^2
        // a^2 + 2abi + b^2 * -1
        // a^2 + 2abi - b^2
        return new Complex(
                new Coordinate(
                        Math.pow(c.real, 2) - Math.pow(c.imaginary, 2),
                        2 * c.real * c.imaginary));
    }

    /**
     * Returns the {@code root}th root of {@code z}.
     *
     * @param root The root.
     * @return {@code z ^ 1/root}
     * @converts Coordinate -> Polar
     */
    public Complex root(double root) {
        return pow(1 / root);
    }

    /**
     * Returns the inverse of the complex number.
     * 
     * @return {@code z ^ -1}
     * @converts Coordinate -> Polar
     */
    public Complex inverse() {
        return pow(-1);
    }

    /**
     * Returns the {@code order}th inverse of the complex number.
     * 
     * @return {@code z ^ (-1 * order)}
     * @param order
     * @converts Coordinate -> Polar
     */
    public Complex inverse(double order) {
        return pow(-1 * order);
    }
}
