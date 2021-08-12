package main.api.response.dialog;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DialogResponseList<T extends AbstractResponse> {
    private String error;
    private long timestamp;
    private long total;
    private int offset;
    private int perPage;
    private List<T> data;

}
