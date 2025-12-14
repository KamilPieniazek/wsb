package com.example.wsb.mailing;

import com.example.wsb.mailing.model.AdminVisitCancellationEvent;
import lombok.RequiredArgsConstructor;
import com.example.wsb.mailing.model.VisitBookedEvent;
import com.example.wsb.mailing.model.VisitCanceledEvent;
import com.example.wsb.mailing.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationMailListener {

    private final MailService mailService;

    @Value("${app.mail.base-url}")
    private String baseUrl;

    @Async
    @TransactionalEventListener
    public void onVisitBooked(final VisitBookedEvent event) {
        String cancelLink = String.format(
                "%s/api/public/cancel/%s?token=%s",
                baseUrl, event.visitId(), event.cancelToken()
        );

       final String body = """
                <html>
                  <body>
                    <h2>Your visit has been confirmed</h2>
                
                    <p>
                      <strong>Date:</strong> %s<br/>
                      <strong>Time:</strong> %s
                    </p>
                
                    <p>
                      To cancel your booking, click the link below:
                    </p>
                
                    <p>
                      <a href="%s">Cancel visit</a>
                    </p>
                  </body>
                </html>
                """.formatted(event.date(), event.time(), cancelLink);

        mailService.sendText(
                event.email(),
                "Booking confirmation",
                body
        );
    }

    @Async
    @TransactionalEventListener
    public void onVisitCanceled(final VisitCanceledEvent event) {
        final String body = """
                <html>
                  <body>
                    <h2>Your visit has been canceled.</h2>
                
                    <p>
                      <strong>Date:</strong> %s<br/>
                      <strong>Time:</strong> %s
                    </p>
                  </body>
                </html>
                """.formatted(event.date(), event.time());

        mailService.sendText(event.email(), "Booking cancellation", body);
    }

    @Async
    @TransactionalEventListener
    public void onVisitCanceledByAdmin(final AdminVisitCancellationEvent event) {

        final String reasonHtml = (event.reason() == null || event.reason().isBlank())
                ? ""
                : """
                <p>
                  <strong>Reason:</strong> %s
                </p>
              """.formatted(event.reason());

        final String body = """
            <html>
              <body>
                <h2>Your visit has been canceled by the administrator.</h2>
            
                <p>
                  <strong>Date:</strong> %s<br/>
                  <strong>Time:</strong> %s
                </p>
            
                %s
              </body>
            </html>
            """.formatted(event.date(), event.time(), reasonHtml);

        mailService.sendText(event.email(), "Booking cancellation", body);
    }
}
