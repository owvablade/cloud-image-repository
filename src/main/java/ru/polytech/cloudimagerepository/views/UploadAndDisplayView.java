package ru.polytech.cloudimagerepository.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import org.springframework.beans.factory.annotation.Autowired;
import ru.polytech.cloudimagerepository.model.ImageData;
import ru.polytech.cloudimagerepository.service.ImageService;
import ru.polytech.cloudimagerepository.views.imagegallery.ImageGalleryViewCard;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Route("")
public class UploadAndDisplayView extends Main {

    private OrderedList imageContainer;

    public UploadAndDisplayView(@Autowired ImageService imageService) {
        constructUI();

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload singleFileUpload = constructSingleFileUpload(memoryBuffer);

        singleFileUpload.addSucceededListener(event -> {
            InputStream fileData = memoryBuffer.getInputStream();
            String fileName = event.getFileName();
            long contentLength = event.getContentLength();
            String mimeType = event.getMIMEType();

            System.out.println(fileName);
            System.out.println(contentLength);
            System.out.println(mimeType);

            List<ImageData> images;
            try {
                images = imageService.findSimilarImages(fileName, mimeType, contentLength, fileData.readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            imageContainer.removeAll();
            for (ImageData imageData : images) {
                System.out.println(imageData.getId() + "/" + imageData.getFilename() + "/" + imageData.getContentType());
                imageContainer.add(new ImageGalleryViewCard(imageData));
            }
            singleFileUpload.clearFileList();
        });

        add(singleFileUpload, imageContainer);
    }

    private void constructUI() {
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        VerticalLayout container = new VerticalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = getHeaderContainer();
        container.add(headerContainer);

        VerticalLayout uploadContainer = getUploadContainer();
        container.add(uploadContainer);

        imageContainer = new OrderedList();
        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.SMALL, Padding.NONE);

        add(container);
    }

    private Upload constructSingleFileUpload(MemoryBuffer memoryBuffer) {
        Upload singleFileUpload = new Upload(memoryBuffer);

        singleFileUpload.setMaxFiles(1);
        singleFileUpload.setAcceptedFileTypes("image/*");

        Button uploadButton = new Button("Upload Image");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Span dropLabel = new Span();
        dropLabel.setText("Images will be uploaded to our cloud.");

        Icon dropIcon = VaadinIcon.CLOUD_UPLOAD_O.create();

        singleFileUpload.setWidth("50%");
        singleFileUpload.setDropLabel(dropLabel);
        singleFileUpload.setDropLabelIcon(dropIcon);
        singleFileUpload.setUploadButton(uploadButton);

        return singleFileUpload;
    }

    private VerticalLayout getHeaderContainer() {
        VerticalLayout headerContainer = new VerticalLayout();

        H1 header = new H1("Cloud Image Repository");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.NONE, FontSize.XXXLARGE);

        Paragraph description = new Paragraph("Upload image and get similar ones by perceptive hash");
        description.addClassNames(Margin.Bottom.NONE, Margin.Top.NONE, TextColor.SECONDARY);

        headerContainer.add(header, description);

        return headerContainer;
    }

    private VerticalLayout getUploadContainer() {
        VerticalLayout uploadContainer = new VerticalLayout();

        H2 uploadHeader = new H2("Upload Image");
        uploadHeader.addClassNames(Margin.Bottom.NONE, Margin.Top.NONE, FontSize.XLARGE);

        Paragraph uploadDescriptionMaxFiles = new Paragraph("Maximum files: 1");
        uploadDescriptionMaxFiles.addClassNames(Margin.Bottom.NONE, Margin.Top.NONE, TextColor.SECONDARY);

        Paragraph uploadDescriptionFileSize = new Paragraph("Maximum file size: unlimited");
        uploadDescriptionFileSize.addClassNames(Margin.Bottom.NONE, Margin.Top.NONE, TextColor.SECONDARY);

        Paragraph uploadDescriptionMimeType = new Paragraph("Acceptable MIME file types: image/*");
        uploadDescriptionMimeType.addClassNames(Margin.Bottom.NONE, Margin.Top.NONE, TextColor.SECONDARY);

        uploadContainer.add(uploadHeader,
                uploadDescriptionMaxFiles,
                uploadDescriptionFileSize,
                uploadDescriptionMimeType);

        return uploadContainer;
    }
}
