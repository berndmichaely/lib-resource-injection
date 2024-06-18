/*
 * Copyright 2024 Bernd Michaely (info@bernd-michaely.de).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.bernd_michaely.common.resources.sample.util;

import de.bernd_michaely.common.resources.BinaryResources;
import de.bernd_michaely.common.resources.FileExt;
import de.bernd_michaely.common.resources.OptionalBinaryObject;
import de.bernd_michaely.common.resources.ResourceHolder;
import de.bernd_michaely.common.resources.ResourceInjector;
import de.bernd_michaely.common.resources.ResourceLoader;
import de.bernd_michaely.common.resources.sample.data.DialogResources;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import static de.bernd_michaely.common.resources.sample.util.GraphicsUtil.DEFAULT_SIZE;

/**
 * Class to create an info about dialog. The dialog is non modal and can change
 * the locale during lifetime.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class InfoAboutDialog
{
	private final Window owner;
	private final ResourceLoader resourceLoader;
	private Stage dialog;
	private final Button buttonClose = new Button();
	private final Label labelInfo = new Label();

	// Callback for ResourceLoader
	private final Consumer<DialogResources> callback = resources ->
	{
		if (dialog != null)
		{
			dialog.setTitle(resources.titleDialog);
			labelInfo.setText(resources.textDialog);
			buttonClose.setText(resources.labelClose);
		}
	};

	@BinaryResources(packagename = "de.bernd_michaely.common.resources.sample.data.binary")
	public static class IconResource extends ResourceHolder
	{
		@FileExt(".png")
		// alternative field annotation: @ResourceKey(value = "iconInfoAbout.png")
		public final OptionalBinaryObject iconInfoAbout = new OptionalBinaryObject();
	}

	/**
	 * The icon image does not change with the locale, so it is not loaded
	 * repeatedly by the {@link ResourceLoader} in the callback method, but
	 * instead only once using a {@link ResourceInjector} directly.
	 *
	 * @return the loaded icon view
	 */
	private Node loadIcon()
	{
		final var iconResource = new IconResource();
		new ResourceInjector().injectResourcesInto(iconResource);
		final var icon = iconResource.iconInfoAbout;
		return icon.isPresent() ? GraphicsUtil.getIcon(icon.get(), true) : new ImageView();
	}

	public InfoAboutDialog(Window owner, ResourceLoader resourceLoader)
	{
		this.owner = owner;
		this.resourceLoader = resourceLoader;
		this.labelInfo.setWrapText(true);
		this.buttonClose.setDefaultButton(true);
		this.buttonClose.setOnAction(e ->
		{
			if (dialog != null)
			{
				dialog.close();
				dialog = null;
			}
		});
	}

	public void init()
	{
		if (dialog == null)
		{
			dialog = new Stage();
			dialog.initOwner(owner);
			final VBox pane = new VBox(2 * DEFAULT_SIZE, loadIcon(), labelInfo, buttonClose);
			pane.setAlignment(Pos.CENTER);
			pane.setPadding(new Insets(2 * DEFAULT_SIZE));
			final Scene scene = new Scene(pane, 400, 450);
			dialog.setScene(scene);
			// register callback for ResourceLoader
			resourceLoader.register(DialogResources.class, callback);
		}
	}

	public void show()
	{
		if (dialog == null)
		{
			init();
			dialog.setOnHidden(e ->
			{
				dialog = null;
				// don't forget to unregister on window close:
				resourceLoader.unregister(callback);
			});
			dialog.show();
		}
		dialog.setIconified(false);
	}
}
