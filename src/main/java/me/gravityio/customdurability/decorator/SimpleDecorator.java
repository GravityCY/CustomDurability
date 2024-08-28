package me.gravityio.customdurability.decorator;

import net.minecraft.network.chat.MutableComponent;

import java.util.Map;
import java.util.function.BiConsumer;

public class SimpleDecorator extends TextDecorator {
    public static SimpleDecorator NEWLINE = new SimpleDecorator((entry, component) -> component.append("\n"));
    public static SimpleDecorator SPACE = new SimpleDecorator((entry, component) -> component.append(" "));


    private final BiConsumer<Map.Entry<String, Integer>, MutableComponent> consumer;

    public SimpleDecorator(BiConsumer<Map.Entry<String, Integer>, MutableComponent> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void onDecorate(Map.Entry<String, Integer> entry, MutableComponent component) {
        this.consumer.accept(entry, component);
    }
}
