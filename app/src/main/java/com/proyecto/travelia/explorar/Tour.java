package com.proyecto.travelia.explorar;

import java.util.Arrays;
import java.util.List;

public class Tour {
    public final String id;
    public final String nombre;
    public final String destino;
    public final String tipo;
    public final double precio;
    public final float rating;
    public final int imageRes;
    public final List<String> fechas;

    public Tour(String id, String nombre, String destino, String tipo, double precio, float rating, int imageRes, String... fechas) {
        this.id = id;
        this.nombre = nombre;
        this.destino = destino;
        this.tipo = tipo;
        this.precio = precio;
        this.rating = rating;
        this.imageRes = imageRes;
        this.fechas = Arrays.asList(fechas);
    }
}
