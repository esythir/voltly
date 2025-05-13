package br.com.fiap.voltly.utils;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DTOMapper {
    private DTOMapper() {}

    public static <E,D> List<D> mapList(List<E> list, Function<? super E,? extends D> fn) {
        return list.stream().map(fn).collect(Collectors.toList());
    }

    public static <E,D> Page<D> mapPage(Page<E> page, Function<? super E,? extends D> fn) {
        return page.map(fn);
    }
}
