package ru.polytech.cloudimagerepository.views.imagegallery;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import ru.polytech.cloudimagerepository.model.ImageData;

import java.io.ByteArrayInputStream;

public class ImageGalleryViewCard extends ListItem {

    public ImageGalleryViewCard(ImageData imageData) {
        addClassNames(Background.CONTRAST_5,
                Display.FLEX,
                FlexDirection.COLUMN,
                AlignItems.START,
                Padding.MEDIUM,
                BorderRadius.LARGE);
        setWidth("50%");

        Div div = new Div();
        div.addClassNames(Background.CONTRAST,
                Display.FLEX,
                AlignItems.CENTER,
                JustifyContent.CENTER,
                Margin.Bottom.MEDIUM,
                Overflow.HIDDEN,
                BorderRadius.NONE,
                Width.AUTO);
        div.setHeight(Height.AUTO);

        Image image = new Image();
        image.setWidth("100%");
        image.setSrc(new StreamResource("similar-image",
                () -> new ByteArrayInputStream(imageData.getImageData())));
        image.setAlt("similar-image");

        div.add(image);

        Span header = new Span();
        header.addClassNames(FontSize.MEDIUM, FontWeight.NORMAL);
        header.setText("Filename: " + imageData.getFilename());

        Span subtitle = new Span();
        subtitle.addClassNames(FontSize.MEDIUM, FontWeight.NORMAL);
        subtitle.setText("Content type: " + imageData.getContentType());

        StreamResource resource = new StreamResource(imageData.getFilename(),
                () -> new ByteArrayInputStream(imageData.getImageData()));

        Anchor downloadLink = new Anchor(resource, "Download Image");
        downloadLink.getElement().setAttribute("download", true);

        add(div, header, subtitle, downloadLink);
    }
}
