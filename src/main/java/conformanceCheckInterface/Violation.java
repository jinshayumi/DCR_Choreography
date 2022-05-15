package conformanceCheckInterface;


public class Violation {
    public boolean assertion;
    public TimedEvent event;

    public Violation(boolean assertion, TimedEvent event) {
        this.assertion = assertion;
        this.event = event;
    }

    public boolean isAssertion() {
        return assertion;
    }

    public void setAssertion(boolean assertion) {
        this.assertion = assertion;
    }

    public TimedEvent getEvent() {
        return event;
    }

    public void setEvent(TimedEvent event) {
        this.event = event;
    }
}
