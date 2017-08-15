package com.hartwig.hmftools.patientreportmailer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.common.annotations.VisibleForTesting;
import com.hartwig.hmftools.common.exception.EmptyFileException;
import com.hartwig.hmftools.common.io.reader.FileReader;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

class ReportMailer {

    private static final String SMTP_HOST = "localhost";
    private static final String SMTP_PORT = "25";

    private static final String MEB_DATE_TAG = "<<meb.date>>";
    private static final String MEB_DEADLINE_TAG = "<<meb.deadline>>";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final LocalDate mebDateSeed = LocalDate.parse("15-08-2017", DATE_FORMATTER);

    private ReportMailer() {
    }

    @NotNull
    static Multipart createMessageBody(@NotNull final String templatePath, @NotNull final String attachmentPath)
            throws IOException, EmptyFileException, MessagingException {
        final Multipart multipart = new MimeMultipart();
        final LocalDate mebDate = determineMebDate();
        final LocalDate mebDeadline = determineMebDeadline();
        final BodyPart messageBodyPart = new MimeBodyPart();
        final String messageBody = fillTemplate(templatePath, mebDate, mebDeadline);
        messageBodyPart.setText(messageBody);

        final BodyPart attachmentBodyPart = new MimeBodyPart();
        final DataSource source = new FileDataSource(attachmentPath);
        attachmentBodyPart.setDataHandler(new DataHandler(source));
        attachmentBodyPart.setFileName(attachmentPath);

        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentBodyPart);
        return multipart;
    }

    static void sendEmail(@NotNull final String subject, @NotNull final Multipart messageBody, @NotNull final String sender,
            @NotNull final String recipients) throws MessagingException {
        final Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        final Session session = Session.getDefaultInstance(props);
        final Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        message.setSubject(subject);
        message.setContent(messageBody);
        Transport.send(message);
    }

    @NotNull
    @VisibleForTesting
    static String fillTemplate(@NotNull final String templatePath, @NotNull final LocalDate mebDate, @NotNull final LocalDate mebDeadline)
            throws IOException, EmptyFileException {
        final List<String> templateLines = FileReader.build().readLines(new File(templatePath).toPath());
        final List<String> emailLines = templateLines.stream()
                .map(line -> line.replaceAll(MEB_DATE_TAG, mebDate.format(DATE_FORMATTER)))
                .map(line -> line.replaceAll(MEB_DEADLINE_TAG, mebDeadline.format(DATE_FORMATTER)))
                .collect(Collectors.toList());
        emailLines.add("\n\n");
        return Strings.join(emailLines, '\n');
    }

    @NotNull
    private static LocalDate determineMebDate() {
        return determineMebDeadline().plusDays(5);
    }

    @NotNull
    private static LocalDate determineMebDeadline() {
        return determineMebDeadline(mebDateSeed, LocalDate.now());
    }

    @NotNull
    @VisibleForTesting
    static LocalDate determineMebDeadline(@NotNull final LocalDate mebSeed, @NotNull final LocalDate currentDate) {
        LocalDate nextDeadline = mebSeed.minusDays(5);
        while (nextDeadline.isBefore(currentDate)) {
            nextDeadline = nextDeadline.plusDays(14);
        }
        return nextDeadline;
    }
}