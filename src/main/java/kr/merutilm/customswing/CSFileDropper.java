package kr.merutilm.customswing;

import javax.annotation.Nullable;

import kr.merutilm.base.util.ConsoleUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public final class CSFileDropper<C extends Component> {
    private boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static final class DropActions<C> {
        private final String extension;
        private final Consumer<C> enterFunction;
        private final BiConsumer<C, File> acceptFunction;

        public DropActions(String extension, Consumer<C> enterFunction, BiConsumer<C, File> acceptFunction) {
            this.extension = extension;
            this.enterFunction = enterFunction;
            this.acceptFunction = acceptFunction;
        }
    }

    private final DropActions<C>[] dropActions;

    @SafeVarargs
    public CSFileDropper(C target, Consumer<C> dropExitActions, DropActions<C>... dropActions) {
        this.dropActions = dropActions;

        new DropTarget(target, new DropTargetListener() {
            @Override
            public void drop(DropTargetDropEvent e) {

                e.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = e.getTransferable();

                DataFlavor[] flavors = transferable.getTransferDataFlavors();

                for (DataFlavor flavor : flavors) {

                    try {
                        if (flavor.isFlavorJavaFileListType()) {
                            List<File> files = (List<File>) transferable.getTransferData(flavor);
                            DropActions<C> action = checkExtension(files.get(0));
                            if (action != null) {
                                action.acceptFunction.accept(target, files.get(0));
                            }

                        }

                    } catch (UnsupportedFlavorException | IOException ex) {
                        ConsoleUtils.logError(ex);
                    }
                }

                // Inform that the drop is complete
                e.dropComplete(true);
            }

            @Override
            public void dragEnter(DropTargetDragEvent e) {
                if (!enabled) {
                    e.rejectDrag();
                    return;
                }

                Transferable transferable = e.getTransferable();
                for (DataFlavor flavor : e.getCurrentDataFlavors()) {
                    if (flavor.isFlavorJavaFileListType()) {
                        try {
                            List<File> files = (List<File>) transferable.getTransferData(flavor);
                            DropActions<C> actions = checkExtension(files.get(0));

                            if (files.size() == 1 && actions != null) {
                                actions.enterFunction.accept(target);
                            } else {
                                e.rejectDrag();
                            }
                        } catch (UnsupportedFlavorException | IOException ex) {
                            ConsoleUtils.logError(ex);
                        }
                    }
                }
            }

            @Override
            public void dragExit(DropTargetEvent e) {
               dropExitActions.accept(target);
            }

            @Override
            public void dragOver(DropTargetDragEvent e) {
                //noop
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent e) {
                //noop
            }
        });
    }

    @Nullable
    private DropActions<C> checkExtension(File file) {
        List<DropActions<C>> availableAction = Arrays.stream(dropActions).filter(e -> file.getAbsolutePath().endsWith(e.extension)).toList();
        if (availableAction.size() == 1) {
            return availableAction.get(0);
        } else if (availableAction.isEmpty()) {
            return null;
        }
        throw new IllegalArgumentException("Duplicate Extensions");
    }
}
