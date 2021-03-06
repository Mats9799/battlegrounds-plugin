package com.matsg.battlegrounds.command;

import com.matsg.battlegrounds.TranslationKey;
import com.matsg.battlegrounds.api.Battlegrounds;
import com.matsg.battlegrounds.api.Translator;
import com.matsg.battlegrounds.command.validator.ValidationResponse;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.*;

public class BattlegroundsCommandTest {

    private Command fakeCommand;
    private CommandSender sender;
    private String fakeCommandName;
    private String responseMessage;
    private Translator translator;

    @Before
    public void setUp() {
        this.sender = mock(CommandSender.class);
        this.fakeCommand = mock(Command.class);
        this.translator = mock(Translator.class);

        this.fakeCommandName = "fake";
        this.responseMessage = "Response";

        when(fakeCommand.getName()).thenReturn(fakeCommandName);
    }

    @Test
    public void executeCommandWithoutArguments() {
        Command help = mock(Command.class);

        when(help.getName()).thenReturn("help");

        BattlegroundsCommand command = new BattlegroundsCommand(translator);
        command.addSubCommand(help);
        command.onCommand(sender, null, "battlegrounds", new String[0]);

        verify(sender, times(0)).sendMessage(anyString());
    }

    @Test
    public void executeSubcommandWhichDoesNotExist() {
        BattlegroundsCommand command = new BattlegroundsCommand(translator);
        TranslationKey key = TranslationKey.INVALID_ARGUMENTS;

        when(translator.translate(key.getPath())).thenReturn(responseMessage);

        command.onCommand(sender, null, "battlegrounds", new String[] { "invalid" });

        verify(sender, times(1)).sendMessage(responseMessage);
    }

    @Test
    public void executePlayerOnlySubcommand() {
        BattlegroundsCommand command = new BattlegroundsCommand(translator);
        CommandSender sender = mock(ConsoleCommandSender.class);
        TranslationKey key = TranslationKey.INVALID_SENDER;

        when(fakeCommand.isPlayerOnly()).thenReturn(true);
        when(translator.translate(key.getPath())).thenReturn(responseMessage);

        command.addSubCommand(fakeCommand);
        command.onCommand(sender, null, "battlegrounds", new String[] { fakeCommandName });

        verify(sender, times(1)).sendMessage(responseMessage);
    }

    @Test
    public void executeSubcommandRequiresPermission() {
        BattlegroundsCommand command = new BattlegroundsCommand(translator);
        String permission = "permission";
        TranslationKey key = TranslationKey.NO_PERMISSION;

        when(fakeCommand.getPermissionNode()).thenReturn(permission);
        when(sender.hasPermission(permission)).thenReturn(false);
        when(translator.translate(key.getPath())).thenReturn(responseMessage);

        command.addSubCommand(fakeCommand);
        command.onCommand(sender, null, "battlegrounds", new String[] { fakeCommandName });

        verify(sender, times(1)).sendMessage(responseMessage);
    }

    @Test
    public void executeSubcommandWithFailedValidation() {
        BattlegroundsCommand command = new BattlegroundsCommand(translator);
        String[] args = new String[] { fakeCommandName };
        ValidationResponse response = new ValidationResponse("Failed");

        when(fakeCommand.validateInput(args)).thenReturn(response);

        command.addSubCommand(fakeCommand);
        command.onCommand(sender, null, "battlegrounds", args);

        verify(fakeCommand, times(0)).execute(sender, args);
        verify(sender, times(1)).sendMessage(response.getMessage());
    }

    @Test
    public void executeSubcommand() {
        BattlegroundsCommand command = new BattlegroundsCommand(translator);
        String[] args = new String[] { fakeCommandName };

        when(fakeCommand.validateInput(args)).thenReturn(ValidationResponse.PASSED);

        command.addSubCommand(fakeCommand);
        command.onCommand(sender, null, "battlegrounds", args);

        verify(fakeCommand, times(1)).execute(sender, args);
    }

    @Test
    public void executeSubcommandWithException() {
        BattlegroundsCommand command = new BattlegroundsCommand(translator);
        String[] args = new String[] { fakeCommandName };
        TranslationKey key = TranslationKey.COMMAND_ERROR;

        doThrow(new RuntimeException()).when(fakeCommand).execute(sender, args);
        when(fakeCommand.validateInput(args)).thenReturn(ValidationResponse.PASSED);

        when(translator.translate(key.getPath())).thenReturn(responseMessage);

        command.addSubCommand(fakeCommand);
        command.onCommand(sender, null, "battlegrounds", args);

        verify(sender, times(1)).sendMessage(responseMessage);
    }
}
