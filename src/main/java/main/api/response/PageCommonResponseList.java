package main.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageCommonResponseList<T> extends CommonResponseList<T> {

    private long total;
    private int offset;
    private int perPage;

    public PageCommonResponseList(String error, long total, int offset, int perPage, T data) {
        super(error, data);
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
    }
}
