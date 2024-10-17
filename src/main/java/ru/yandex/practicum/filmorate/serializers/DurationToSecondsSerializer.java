package ru.yandex.practicum.filmorate.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Duration;

public class DurationToSecondsSerializer extends StdSerializer<Duration> {

    public DurationToSecondsSerializer() {
        this(null);
    }

    protected DurationToSecondsSerializer(Class<Duration> t) {
        super(t);
    }

    @Override
    public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(duration.toSeconds());
    }
}