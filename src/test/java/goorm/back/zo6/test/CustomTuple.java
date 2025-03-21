//package goorm.back.zo6.test;
//
//import jakarta.persistence.Tuple;
//import jakarta.persistence.TupleElement;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class CustomTuple implements Tuple {
//    private final Map<Integer, Object> indexMap = new HashMap<>();
//    private final Map<String, Object> aliasMap = new HashMap<>();
//
//    public CustomTuple(Object... values) {
//        for (int i = 0; i < values.length; i++) {
//            indexMap.put(i, values[i]); // 숫자 인덱스로 저장
//        }
//    }
//
//    @Override
//    public <T> T get(int index, Class<T> type) {
//        return type.cast(indexMap.get(index));
//    }
//
//    @Override
//    public <X> X get(TupleElement<X> tupleElement) {
//        return null;
//    }
//
//    @Override
//    public <T> T get(String alias, Class<T> type) {
//        return type.cast(aliasMap.get(alias));
//    }
//
//    @Override
//    public Object get(int index) {
//        return indexMap.get(index);
//    }
//
//    @Override
//    public Object[] toArray() {
//        return new Object[0];
//    }
//
//    @Override
//    public Object get(String alias) {
//        return aliasMap.get(alias);
//    }
//
//    @Override
//    public List<TupleElement<?>> getElements() {
//        return List.of(); // 필요 없으면 빈 리스트 반환
//    }
//}
