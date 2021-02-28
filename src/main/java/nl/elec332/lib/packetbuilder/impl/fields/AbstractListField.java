package nl.elec332.lib.packetbuilder.impl.fields;

import nl.elec332.lib.packetbuilder.api.util.IValueReference;
import nl.elec332.lib.packetbuilder.util.ValueReference;

import java.util.*;

/**
 * Created by Elec332 on 2/28/2021
 */
public abstract class AbstractListField<T> extends AbstractSimpleField<List<T>> implements List<T> {

    @SafeVarargs
    public AbstractListField(T... defaultValues) {
        this(Arrays.asList(defaultValues));
    }

    public AbstractListField(List<T> defaultValue) {
        this(new ValueReference<>(defaultValue));
    }

    public AbstractListField(IValueReference<List<T>> reference) {
        super(reference);
    }

    @Override
    public int size() {
        return this.get().size();
    }

    @Override
    public boolean isEmpty() {
        return this.get().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.get().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.get().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.get().toArray();
    }

    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public <T1> T1[] toArray(T1[] a) {
        return this.get().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return this.get().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.get().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.get().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.get().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return this.get().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.get().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.get().retainAll(c);
    }

    @Override
    public void clear() {
        this.get().clear();
    }

    @Override
    public T get(int index) {
        return this.get().get(index);
    }

    @Override
    public T set(int index, T element) {
        return this.get().set(index, element);
    }

    @Override
    public void add(int index, T element) {
        this.get().add(index, element);
    }

    @Override
    public T remove(int index) {
        return this.get().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.get().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.get().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.get().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return this.get().listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return this.get().subList(fromIndex, toIndex);
    }

}
