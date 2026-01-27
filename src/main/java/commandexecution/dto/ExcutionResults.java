package commandexecution.dto;

import java.io.PipedInputStream;

public record ExcutionResults(PipedInputStream inputStream,PipedInputStream errorStream) {
}
