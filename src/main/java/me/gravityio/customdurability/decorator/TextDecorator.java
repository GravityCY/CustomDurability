package me.gravityio.customdurability.decorator;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Map;

public abstract class TextDecorator {
    protected RegistryAccess registry;

    public TextDecorator(RegistryAccess registry) {
        this.registry = registry;
    }

    public TextDecorator() {
    }

    public void decorate(Map.Entry<String, Integer> entry, MutableComponent component) {
        this.onDecorate(entry, component);
    }

    protected abstract void onDecorate(Map.Entry<String, Integer> entry, MutableComponent component);

    public DecoratorList create() {
        return new DecoratorList(this);
    }

    public static class DecoratorList {
        private final ArrayList<TextDecorator> decoratorList = new ArrayList<>();


        public DecoratorList(TextDecorator initial) {
            this.decoratorList.add(initial);
        }

        public void decorate(Map.Entry<String, Integer> entry, MutableComponent component) {
            for (TextDecorator textDecorator : this.decoratorList) {
                textDecorator.decorate(entry, component);
            }
        }

        public DecoratorList then(TextDecorator decorator) {
            this.decoratorList.add(decorator);
            return this;
        }
    }
}
