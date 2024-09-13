package de.api;

import java.io.OutputStream;
import java.util.List;

public interface Renderer {

    void render(final OutputStream outputStream, final List<Part> parts);
}
