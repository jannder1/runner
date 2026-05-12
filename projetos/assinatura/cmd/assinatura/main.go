package main

import (
	"fmt"
	"os"

	"github.com/spf13/cobra"
)

var version = "dev"

func main() {

	var rootCmd = &cobra.Command{
		Use:   "assinatura",
		Short: "CLI do Sistema Runner",
		Long:  "Sistema de assinatura digital simulada",
	}

	var versionCmd = &cobra.Command{
		Use:   "version",
		Short: "Mostra a versão da aplicação",
		Run: func(cmd *cobra.Command, args []string) {
			fmt.Printf("assinatura version %s\n", version)
		},
	}

	rootCmd.AddCommand(versionCmd)

	if err := rootCmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
}
